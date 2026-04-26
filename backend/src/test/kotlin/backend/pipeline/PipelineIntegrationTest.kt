package backend.pipeline

import backend.domain.champion.ChampionRepository
import backend.domain.match.JungleMatchRecordRepository
import backend.domain.match.PureFullcampClearRecordRepository
import backend.domain.stats.ChampionClearStatsRepository
import backend.domain.stats.ChampionMatchupRepository
import backend.domain.stats.ChampionStatsRepository
import backend.riot.RiotRateLimiter
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.time.LocalDate

/**
 * Full pipeline integration test (Step 0~5) with H2 + MockWebServer.
 *
 * Test data layout:
 *   Users: 3 Challenger (c1,c2,c3) + 2 Grandmaster (g1,g2) = 5
 *   Matches: KR_8001~8006 (6 unique, KR_8005 is remake)
 *   Valid matches: 5 → 10 jungle_match_record
 *   Pure clears: 7 / Impure: 3
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(PipelineIntegrationTest.TestConfig::class)
class PipelineIntegrationTest {

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun testRateLimiter() = RiotRateLimiter(minIntervalMs = 0)
    }

    companion object {
        val mockServer = MockWebServer().apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            val url = mockServer.url("/").toString().trimEnd('/')
            registry.add("riot.kr-base-url") { url }
            registry.add("riot.asia-base-url") { url }
            registry.add("riot.ddragon-base-url") { url }
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            mockServer.shutdown()
        }
    }

    @Autowired lateinit var pipeline: MatchCollectionPipeline
    @Autowired lateinit var championRepo: ChampionRepository
    @Autowired lateinit var matchRecordRepo: JungleMatchRecordRepository
    @Autowired lateinit var clearRecordRepo: PureFullcampClearRecordRepository
    @Autowired lateinit var statsRepo: ChampionStatsRepository
    @Autowired lateinit var matchupRepo: ChampionMatchupRepository
    @Autowired lateinit var clearStatsRepo: ChampionClearStatsRepository

    @BeforeEach
    fun setUp() {
        clearStatsRepo.deleteAll()
        matchupRepo.deleteAll()
        statsRepo.deleteAll()
        clearRecordRepo.deleteAll()
        matchRecordRepo.deleteAll()
        championRepo.deleteAll()
        mockServer.dispatcher = createDispatcher()
    }

    @Test
    fun `full pipeline Step 0 to 5 produces expected data`(): Unit = runBlocking {
        val result = pipeline.execute(LocalDate.of(2026, 4, 25))

        // Step 0: Champion sync — 6 new champions
        assertThat(result.championSync.total).isEqualTo(6)
        assertThat(result.championSync.created).isEqualTo(6)
        assertThat(championRepo.findAll().map { it.id }).containsExactlyInAnyOrder(
            "LeeSin", "Graves", "Nidalee", "Hecarim", "Viego", "Aatrox"
        )

        // Step 1: 5 users collected
        assertThat(result.userPoolSize).isEqualTo(5)

        // Step 2: 6 unique match IDs
        assertThat(result.uniqueMatchIds).isEqualTo(6)

        // Step 3: 10 records saved (5 valid × 2), 1 remake skipped
        assertThat(result.processResult.savedCount).isEqualTo(10)
        assertThat(result.processResult.skippedRemake).isEqualTo(1)
        assertThat(result.processResult.skippedNoJungler).isEqualTo(0)
        assertThat(result.processResult.errors).isEqualTo(0)

        val records = matchRecordRepo.findAll()
        assertThat(records).hasSize(10)

        val leeSin8001 = records.find { it.matchId == "KR_8001" && it.championId == "LeeSin" }!!
        assertThat(leeSin8001.team).isEqualTo("BLUE")
        assertThat(leeSin8001.win).isTrue()
        assertThat(leeSin8001.opponentChampionId).isEqualTo("Graves")
        assertThat(leeSin8001.patchVersion).isEqualTo("16.8")
        assertThat(leeSin8001.summonerTier).isEqualTo("CHALLENGER")

        // Step 4: 5 timelines analyzed, 7 pure / 3 impure
        assertThat(result.timelineResult.analyzed).isEqualTo(5)
        assertThat(result.timelineResult.clearRecordsCreated).isEqualTo(7)
        assertThat(result.timelineResult.purityFailed).isEqualTo(3)
        assertThat(result.timelineResult.errors).isEqualTo(0)

        assertThat(clearRecordRepo.findAll()).hasSize(7)

        val updatedRecords = matchRecordRepo.findAll()
        assertThat(updatedRecords.count { it.startPosition != null }).isEqualTo(10)
        assertThat(updatedRecords.find { it.matchId == "KR_8001" && it.championId == "LeeSin" }!!.startPosition)
            .isEqualTo("BLUE_BUFF")
        assertThat(updatedRecords.find { it.matchId == "KR_8004" && it.championId == "Graves" }!!.startPosition)
            .isEqualTo("RAPTORS")

        // Step 5: Stats aggregation
        assertThat(result.statsResult.statsCount).isEqualTo(5)
        assertThat(result.statsResult.matchupCount).isEqualTo(10)
        assertThat(result.statsResult.clearStatsCount).isEqualTo(10)

        val stats = statsRepo.findAll()

        val leeSinStats = stats.find { it.championId == "LeeSin" }!!
        assertThat(leeSinStats.pickCount).isEqualTo(2)
        assertThat(leeSinStats.winCount).isEqualTo(1)
        assertThat(leeSinStats.banCount).isEqualTo(1)
        assertThat(leeSinStats.totalMatches).isEqualTo(5)

        val matchups = matchupRepo.findAll()
        assertThat(matchups).hasSize(10)
        val lsVsGr = matchups.find { it.championId == "LeeSin" && it.opponentChampionId == "Graves" }!!
        assertThat(lsVsGr.wins).isEqualTo(1)
        assertThat(lsVsGr.losses).isEqualTo(0)

        val clearStats = clearStatsRepo.findAll()
        assertThat(clearStats).hasSize(10)
        val lsBlueClear = clearStats.find {
            it.championId == "LeeSin" && it.team == "BLUE" && it.startPosition == "BLUE_BUFF"
        }!!
        assertThat(lsBlueClear.avgClearTimeMs).isEqualTo(180000)
        assertThat(lsBlueClear.bestClearTimeMs).isEqualTo(180000)
        assertThat(lsBlueClear.sampleCount).isEqualTo(1)
        assertThat(lsBlueClear.startCount).isEqualTo(1)
        assertThat(lsBlueClear.totalGames).isEqualTo(1)
    }

    // ==================== MockWebServer Dispatcher ====================

    private fun createDispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: return MockResponse().setResponseCode(404)
            return when {
                path == "/api/versions.json" ->
                    json("""["16.8.1","16.7.1"]""")

                path.contains("/data/ko_KR/champion.json") ->
                    json(championsJson())

                path.contains("/challengerleagues/") ->
                    json(leagueJson("CHALLENGER", listOf("puuid-c1" to 2400, "puuid-c2" to 2200, "puuid-c3" to 2000)))

                path.contains("/grandmasterleagues/") ->
                    json(leagueJson("GRANDMASTER", listOf("puuid-g1" to 800, "puuid-g2" to 700)))

                path.contains("/by-puuid/") && path.contains("/ids") -> {
                    val puuid = path.substringAfter("/by-puuid/").substringBefore("/ids")
                    json(matchIdsFor(puuid))
                }

                path.contains("/matches/") && path.contains("/timeline") -> {
                    val id = path.substringAfter("/matches/").substringBefore("/timeline")
                    json(timelineFor(id))
                }

                path.contains("/matches/") && !path.contains("/by-puuid/") -> {
                    val id = path.substringAfter("/matches/").substringBefore("?")
                    json(matchDetailFor(id))
                }

                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    // ==================== JSON Response Builders ====================

    private fun json(body: String) = MockResponse()
        .setBody(body)
        .setHeader("Content-Type", "application/json")

    private fun championsJson() = """
        {"version":"16.8.1","data":{
          "LeeSin":{"id":"LeeSin","key":"64","name":"리 신","title":"눈먼 수도승"},
          "Graves":{"id":"Graves","key":"104","name":"그레이브즈","title":"무법자"},
          "Nidalee":{"id":"Nidalee","key":"76","name":"니달리","title":"야성의 사냥꾼"},
          "Hecarim":{"id":"Hecarim","key":"120","name":"헤카림","title":"전쟁의 전령"},
          "Viego":{"id":"Viego","key":"234","name":"비에고","title":"몰락한 왕"},
          "Aatrox":{"id":"Aatrox","key":"266","name":"아트록스","title":"다르킨의 검"}
        }}""".trimIndent()

    private fun leagueJson(tier: String, entries: List<Pair<String, Int>>) =
        """{"tier":"$tier","entries":[${
            entries.joinToString(",") { (p, lp) -> """{"puuid":"$p","leaguePoints":$lp}""" }
        }]}"""

    // ---- Match IDs per puuid ----
    // puuid-c1 → 8001,8002  puuid-c2 → 8001,8003  puuid-c3 → 8004,8005
    // puuid-g1 → 8002,8003  puuid-g2 → 8004,8006
    // Unique: 8001~8006

    private fun matchIdsFor(puuid: String) = when (puuid) {
        "puuid-c1" -> """["KR_8001","KR_8002"]"""
        "puuid-c2" -> """["KR_8001","KR_8003"]"""
        "puuid-c3" -> """["KR_8004","KR_8005"]"""
        "puuid-g1" -> """["KR_8002","KR_8003"]"""
        "puuid-g2" -> """["KR_8004","KR_8006"]"""
        else -> "[]"
    }

    // ---- Match Details ----
    // KR_8001: LeeSin(BLUE,W) vs Graves(RED,L)   | bans: Viego,Nidalee / Hecarim
    // KR_8002: Nidalee(BLUE,L) vs Hecarim(RED,W)  | bans: Graves / Viego
    // KR_8003: Viego(BLUE,W) vs LeeSin(RED,L)     | bans: Hecarim / Nidalee
    // KR_8004: Graves(BLUE,W) vs Nidalee(RED,L)   | bans: — / Viego
    // KR_8005: REMAKE (180s)
    // KR_8006: Hecarim(BLUE,W) vs Viego(RED,L)    | bans: LeeSin / Graves

    private fun matchDetailFor(matchId: String) = when (matchId) {
        "KR_8001" -> matchJson("KR_8001", "puuid-c1", "LeeSin", true, "puuid-c2", "Graves", false, 1800, listOf(234, -1, 76, -1, -1), listOf(120, -1, -1, -1, -1))
        "KR_8002" -> matchJson("KR_8002", "puuid-c1", "Nidalee", false, "puuid-g1", "Hecarim", true, 2000, listOf(104, -1, -1, -1, -1), listOf(234, -1, -1, -1, -1))
        "KR_8003" -> matchJson("KR_8003", "puuid-c2", "Viego", true, "puuid-g1", "LeeSin", false, 1500, listOf(120, -1, -1, -1, -1), listOf(76, -1, -1, -1, -1))
        "KR_8004" -> matchJson("KR_8004", "puuid-c3", "Graves", true, "puuid-g2", "Nidalee", false, 2200, listOf(-1, -1, -1, -1, -1), listOf(234, -1, -1, -1, -1))
        "KR_8005" -> matchJson("KR_8005", "puuid-c3", "Viego", true, "puuid-g2", "LeeSin", false, 180, emptyList(), emptyList())
        "KR_8006" -> matchJson("KR_8006", "puuid-g2", "Hecarim", true, "puuid-c3", "Viego", false, 1900, listOf(64, -1, -1, -1, -1), listOf(104, -1, -1, -1, -1))
        else -> """{"metadata":{"matchId":"$matchId","participants":[]},"info":{"gameVersion":"16.8.1","gameDuration":100,"gameStartTimestamp":0,"participants":[],"teams":[]}}"""
    }

    private fun matchJson(
        matchId: String,
        p1Puuid: String, p1Champ: String, p1Win: Boolean,
        p2Puuid: String, p2Champ: String, p2Win: Boolean,
        duration: Long, blueBans: List<Int>, redBans: List<Int>
    ): String {
        fun bans(ids: List<Int>, off: Int) =
            ids.mapIndexed { i, id -> """{"championId":$id,"pickTurn":${i + off}}""" }.joinToString(",")
        val bb = if (blueBans.isNotEmpty()) bans(blueBans, 1) else ""
        val rb = if (redBans.isNotEmpty()) bans(redBans, 6) else ""
        return """
        {"metadata":{"matchId":"$matchId","participants":["$p1Puuid","_2","_3","_4","_5","$p2Puuid","_7","_8","_9","_10"]},
         "info":{"gameVersion":"16.8.123.456","gameDuration":$duration,"gameStartTimestamp":1745546400000,
          "participants":[
            {"puuid":"$p1Puuid","riotIdGameName":"P${p1Puuid.takeLast(2)}","riotIdTagline":"KR1","championName":"$p1Champ","teamId":100,"teamPosition":"JUNGLE","win":$p1Win,"perks":{"styles":[]},"summoner1Id":11,"summoner2Id":4},
            {"puuid":"$p2Puuid","riotIdGameName":"P${p2Puuid.takeLast(2)}","riotIdTagline":"KR1","championName":"$p2Champ","teamId":200,"teamPosition":"JUNGLE","win":$p2Win,"perks":{"styles":[]},"summoner1Id":4,"summoner2Id":11}
          ],
          "teams":[{"teamId":100,"bans":[$bb]},{"teamId":200,"bans":[$rb]}]
        }}""".trimIndent()
    }

    // ---- Timelines ----
    // Purity: KR_8001(both✓) KR_8002(Nidalee✓,Hecarim✗kill) KR_8003(Viego✗died,LeeSin✓)
    //         KR_8004(both✓) KR_8006(Hecarim✗minions,Viego✓)
    // Start positions (BLUE side camps / RED side camps):
    //   8001: BLUE_BUFF / RED_BUFF    8002: WOLVES / RAPTORS
    //   8003: RED_BUFF / BLUE_BUFF    8004: RAPTORS / WOLVES
    //   8006: GROMP / KRUGS

    private fun timelineFor(matchId: String) = when (matchId) {
        "KR_8001" -> tlJson("KR_8001", "puuid-c1", "puuid-c2",
            3800 to 7900, 7450 to 10830, 180000)
        "KR_8002" -> tlJson("KR_8002", "puuid-c1", "puuid-g1",
            3780 to 6440, 7850 to 9380, 195000,
            frame2Events = """{"type":"CHAMPION_KILL","timestamp":150000,"killerId":6,"victimId":3}""")
        "KR_8003" -> tlJson("KR_8003", "puuid-c2", "puuid-g1",
            7460 to 3930, 10930 to 7070, 190000,
            frame2Events = """{"type":"CHAMPION_KILL","timestamp":140000,"killerId":3,"victimId":1}""")
        "KR_8004" -> tlJson("KR_8004", "puuid-c3", "puuid-g2",
            6970 to 5420, 11000 to 8420, 185000)
        "KR_8006" -> tlJson("KR_8006", "puuid-g2", "puuid-c3",
            2170 to 8350, 6470 to 12000, 200000,
            j1Frame2Minions = 5)
        else -> """{"metadata":{"matchId":"$matchId","participants":[]},"info":{"frameInterval":60000,"participants":[],"frames":[]}}"""
    }

    private fun tlJson(
        matchId: String,
        j1Puuid: String, j6Puuid: String,
        j1Pos: Pair<Int, Int>, j6Pos: Pair<Int, Int>,
        lvl4Time: Long,
        frame2Events: String = "",
        j1Frame2Minions: Int = 0, j1Frame2Damage: Long = 0,
        j6Frame2Minions: Int = 0, j6Frame2Damage: Long = 0
    ): String {
        val ev = if (frame2Events.isNotEmpty()) frame2Events else ""
        return """
        {"metadata":{"matchId":"$matchId","participants":["$j1Puuid","_2","_3","_4","_5","$j6Puuid","_7","_8","_9","_10"]},
         "info":{"frameInterval":60000,
          "participants":[{"participantId":1,"puuid":"$j1Puuid"},{"participantId":6,"puuid":"$j6Puuid"}],
          "frames":[
            {"timestamp":0,"events":[],"participantFrames":{}},
            {"timestamp":60000,"events":[],"participantFrames":{
              "1":{"participantId":1,"position":{"x":${j1Pos.first},"y":${j1Pos.second}},"level":2,"minionsKilled":0,"damageStats":{"totalDamageDoneToChampions":0}},
              "6":{"participantId":6,"position":{"x":${j6Pos.first},"y":${j6Pos.second}},"level":2,"minionsKilled":0,"damageStats":{"totalDamageDoneToChampions":0}}
            }},
            {"timestamp":120000,"events":[$ev],"participantFrames":{
              "1":{"participantId":1,"position":{"x":5000,"y":5000},"level":3,"minionsKilled":$j1Frame2Minions,"damageStats":{"totalDamageDoneToChampions":$j1Frame2Damage}},
              "6":{"participantId":6,"position":{"x":9000,"y":9000},"level":3,"minionsKilled":$j6Frame2Minions,"damageStats":{"totalDamageDoneToChampions":$j6Frame2Damage}}
            }},
            {"timestamp":$lvl4Time,"events":[
              {"type":"LEVEL_UP","timestamp":$lvl4Time,"participantId":1,"level":4},
              {"type":"LEVEL_UP","timestamp":$lvl4Time,"participantId":6,"level":4}
            ],"participantFrames":{
              "1":{"participantId":1,"position":{"x":5000,"y":5000},"level":4,"minionsKilled":0,"damageStats":{"totalDamageDoneToChampions":0}},
              "6":{"participantId":6,"position":{"x":9000,"y":9000},"level":4,"minionsKilled":0,"damageStats":{"totalDamageDoneToChampions":0}}
            }}
          ]
        }}""".trimIndent()
    }
}
