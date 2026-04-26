package backend.pipeline

import backend.domain.match.JungleMatchRecord
import backend.jpa.JungleMatchRecordJpaService
import backend.riot.RiotApiClient
import backend.riot.dto.*
import tools.jackson.databind.JsonNode
import tools.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MatchDetailProcessorTest {

    private val riotApiClient = mockk<RiotApiClient>()
    private val jungleMatchRecordJpaService = mockk<JungleMatchRecordJpaService>()
    private val objectMapper = jacksonObjectMapper()
    private val processor = MatchDetailProcessor(riotApiClient, jungleMatchRecordJpaService, objectMapper)

    private val championKeyToIdMap = mapOf(
        64 to "LeeSin",
        104 to "Graves",
        76 to "Nidalee",
        120 to "Hecarim",
        234 to "Viego"
    )

    private var savedRecordIdCounter = 1L

    @BeforeEach
    fun setUp() {
        every { jungleMatchRecordJpaService.saveAll(any()) } answers {
            val records = firstArg<List<JungleMatchRecord>>()
            records.map { record ->
                JungleMatchRecord(
                    id = savedRecordIdCounter++,
                    matchId = record.matchId,
                    championId = record.championId,
                    team = record.team,
                    win = record.win,
                    opponentChampionId = record.opponentChampionId,
                    runes = record.runes,
                    summonerSpells = record.summonerSpells,
                    bannedChampions = record.bannedChampions,
                    patchVersion = record.patchVersion,
                    summonerName = record.summonerName,
                    summonerTag = record.summonerTag,
                    summonerTier = record.summonerTier,
                    gameStartedAt = record.gameStartedAt
                )
            }
        }
        savedRecordIdCounter = 1L
    }

    @Test
    fun `skip remake matches with gameDuration under 300`() = runTest {
        coEvery { riotApiClient.getMatchDetail("KR_9999") } returns createMatchDetail(gameDuration = 180)

        val result = processor.process(setOf("KR_9999"), emptyMap(), championKeyToIdMap)

        assertThat(result.skippedRemake).isEqualTo(1)
        assertThat(result.savedCount).isEqualTo(0)
    }

    @Test
    fun `skip matches without exactly 2 junglers`() = runTest {
        coEvery { riotApiClient.getMatchDetail("KR_1") } returns createMatchDetail(
            junglerCount = 1, gameDuration = 1800
        )

        val result = processor.process(setOf("KR_1"), emptyMap(), championKeyToIdMap)

        assertThat(result.skippedNoJungler).isEqualTo(1)
        assertThat(result.savedCount).isEqualTo(0)
    }

    @Test
    fun `extract two match records per match and return jungler mappings`() = runTest {
        coEvery { riotApiClient.getMatchDetail("KR_1") } returns createNormalMatchDetail()

        val result = processor.process(setOf("KR_1"), emptyMap(), championKeyToIdMap)

        assertThat(result.savedCount).isEqualTo(2)
        assertThat(result.junglersByMatch).containsKey("KR_1")
        assertThat(result.junglersByMatch["KR_1"]).hasSize(2)
    }

    @Test
    fun `parse patch version correctly`() = runTest {
        coEvery { riotApiClient.getMatchDetail("KR_1") } returns createNormalMatchDetail(
            gameVersion = "16.6.756.9613"
        )

        val result = processor.process(setOf("KR_1"), emptyMap(), championKeyToIdMap)
        val mappings = result.junglersByMatch["KR_1"]!!
        assertThat(mappings).allMatch { it.team == "BLUE" || it.team == "RED" }
    }

    @Test
    fun `set summoner_tier from user pool`() = runTest {
        val userPool = mapOf("puuid-jungle-1" to "CHALLENGER")
        coEvery { riotApiClient.getMatchDetail("KR_1") } returns createNormalMatchDetail()

        val result = processor.process(setOf("KR_1"), userPool, championKeyToIdMap)

        val mappings = result.junglersByMatch["KR_1"]!!
        assertThat(mappings.map { it.puuid }).containsExactlyInAnyOrder("puuid-jungle-1", "puuid-jungle-2")
    }

    @Test
    fun `format summoner spells as comma-separated ids`() = runTest {
        coEvery { riotApiClient.getMatchDetail("KR_1") } returns createNormalMatchDetail()

        val result = processor.process(setOf("KR_1"), emptyMap(), championKeyToIdMap)
        assertThat(result.savedCount).isEqualTo(2)
    }

    private fun createMatchDetail(gameDuration: Long = 1800, junglerCount: Int = 2): MatchDetailDto {
        val participants = (1..junglerCount).map { i ->
            ParticipantDto(
                puuid = "puuid-$i",
                riotIdGameName = "Player$i",
                riotIdTagline = "KR1",
                championName = "Champ$i",
                teamId = if (i == 1) 100 else 200,
                teamPosition = "JUNGLE",
                win = i == 1,
                summoner1Id = 11,
                summoner2Id = 4
            )
        }
        return MatchDetailDto(
            metadata = MatchMetadataDto(matchId = "KR_9999"),
            info = MatchInfoDto(
                gameVersion = "16.6.756.9613",
                gameDuration = gameDuration,
                gameStartTimestamp = 1714000000000,
                participants = participants,
                teams = emptyList()
            )
        )
    }

    private fun createNormalMatchDetail(gameVersion: String = "16.6.756.9613"): MatchDetailDto {
        val perksNode: JsonNode = objectMapper.readTree("""{"styles":[]}""")
        return MatchDetailDto(
            metadata = MatchMetadataDto(matchId = "KR_1"),
            info = MatchInfoDto(
                gameVersion = gameVersion,
                gameDuration = 1800,
                gameStartTimestamp = 1714000000000,
                participants = listOf(
                    ParticipantDto(
                        puuid = "puuid-jungle-1",
                        riotIdGameName = "Hide on bush",
                        riotIdTagline = "KR1",
                        championName = "LeeSin",
                        teamId = 100,
                        teamPosition = "JUNGLE",
                        win = true,
                        perks = perksNode,
                        summoner1Id = 11,
                        summoner2Id = 4
                    ),
                    ParticipantDto(
                        puuid = "puuid-jungle-2",
                        riotIdGameName = "Canyon",
                        riotIdTagline = "KR1",
                        championName = "Graves",
                        teamId = 200,
                        teamPosition = "JUNGLE",
                        win = false,
                        perks = perksNode,
                        summoner1Id = 4,
                        summoner2Id = 11
                    )
                ),
                teams = listOf(
                    TeamDto(teamId = 100, bans = listOf(BanDto(championId = 234, pickTurn = 1), BanDto(championId = -1, pickTurn = 2))),
                    TeamDto(teamId = 200, bans = listOf(BanDto(championId = 120, pickTurn = 3)))
                )
            )
        )
    }
}
