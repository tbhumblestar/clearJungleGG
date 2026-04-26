package backend.pipeline

import backend.domain.match.JungleMatchRecord
import backend.domain.match.PureFullcampClearRecord
import backend.domain.stats.ChampionClearStats
import backend.domain.stats.ChampionMatchup
import backend.domain.stats.ChampionStats
import backend.jpa.JungleMatchRecordJpaService
import backend.jpa.PureFullcampClearRecordJpaService
import backend.jpa.StatsJpaService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant

class StatsAggregatorTest {

    private val jungleMatchRecordJpaService = mockk<JungleMatchRecordJpaService>()
    private val pureFullcampClearRecordJpaService = mockk<PureFullcampClearRecordJpaService>()
    private val statsJpaService = mockk<StatsJpaService>(relaxed = true)
    private val objectMapper = jacksonObjectMapper()
    private val aggregator = StatsAggregator(jungleMatchRecordJpaService, pureFullcampClearRecordJpaService, statsJpaService, objectMapper)

    @Test
    fun `aggregate computes pick count and win count`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6"),
            createRecord(2, "KR_1", "Graves", "LeeSin", false, "16.6"),
            createRecord(3, "KR_2", "LeeSin", "Nidalee", false, "16.6"),
            createRecord(4, "KR_2", "Nidalee", "LeeSin", true, "16.6")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns emptyList()

        val statsSlot = slot<List<ChampionStats>>()
        every { statsJpaService.replaceChampionStats("16.6", capture(statsSlot)) } just Runs

        aggregator.aggregate()

        val stats = statsSlot.captured
        val leeSin = stats.first { it.championId == "LeeSin" }
        assertThat(leeSin.pickCount).isEqualTo(2)
        assertThat(leeSin.winCount).isEqualTo(1)
        assertThat(leeSin.totalMatches).isEqualTo(2)
    }

    @Test
    fun `aggregate computes ban count with match deduplication`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6", """["Nidalee","Hecarim"]"""),
            createRecord(2, "KR_1", "Graves", "LeeSin", false, "16.6", """["Nidalee","Hecarim"]""")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns emptyList()

        val statsSlot = slot<List<ChampionStats>>()
        every { statsJpaService.replaceChampionStats("16.6", capture(statsSlot)) } just Runs

        aggregator.aggregate()

        val stats = statsSlot.captured
        val nidalee = stats.find { it.championId == "Nidalee" }
        assertThat(nidalee?.banCount).isEqualTo(1)
    }

    @Test
    fun `aggregate computes matchup wins and losses`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6"),
            createRecord(2, "KR_1", "Graves", "LeeSin", false, "16.6")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns emptyList()

        val matchupSlot = slot<List<ChampionMatchup>>()
        every { statsJpaService.replaceChampionMatchups("16.6", capture(matchupSlot)) } just Runs

        aggregator.aggregate()

        val matchups = matchupSlot.captured
        val leeSinVsGraves = matchups.first { it.championId == "LeeSin" && it.opponentChampionId == "Graves" }
        assertThat(leeSinVsGraves.wins).isEqualTo(1)
        assertThat(leeSinVsGraves.losses).isEqualTo(0)

        val gravesVsLeeSin = matchups.first { it.championId == "Graves" && it.opponentChampionId == "LeeSin" }
        assertThat(gravesVsLeeSin.wins).isEqualTo(0)
        assertThat(gravesVsLeeSin.losses).isEqualTo(1)
    }

    @Test
    fun `aggregate computes avg and best clear time per champion-team-position`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6", startPosition = "RED_BUFF", team = "BLUE"),
            createRecord(2, "KR_1", "Graves", "LeeSin", false, "16.6", startPosition = "BLUE_BUFF", team = "RED"),
            createRecord(3, "KR_2", "LeeSin", "Nidalee", false, "16.6", startPosition = "RED_BUFF", team = "BLUE"),
            createRecord(4, "KR_2", "Nidalee", "LeeSin", true, "16.6", startPosition = "RED_BUFF", team = "RED")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns listOf(
            PureFullcampClearRecord(id = 1, matchRecordId = 1, clearTimeMs = 200000),
            PureFullcampClearRecord(id = 2, matchRecordId = 3, clearTimeMs = 190000)
        )

        val clearStatsSlot = slot<List<ChampionClearStats>>()
        every { statsJpaService.replaceChampionClearStats("16.6", capture(clearStatsSlot)) } just Runs

        aggregator.aggregate()

        val clearStats = clearStatsSlot.captured
        val leeSinBlueRed = clearStats.first { it.championId == "LeeSin" && it.team == "BLUE" && it.startPosition == "RED_BUFF" }
        assertThat(leeSinBlueRed.avgClearTimeMs).isEqualTo(195000)
        assertThat(leeSinBlueRed.bestClearTimeMs).isEqualTo(190000)
        assertThat(leeSinBlueRed.sampleCount).isEqualTo(2)
        assertThat(leeSinBlueRed.startCount).isEqualTo(2)
        assertThat(leeSinBlueRed.totalGames).isEqualTo(2)
    }

    @Test
    fun `aggregate filters out records with null start position from clear stats`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6", startPosition = null, team = "BLUE")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns listOf(
            PureFullcampClearRecord(id = 1, matchRecordId = 1, clearTimeMs = 200000)
        )

        val clearStatsSlot = slot<List<ChampionClearStats>>()
        every { statsJpaService.replaceChampionClearStats("16.6", capture(clearStatsSlot)) } just Runs

        aggregator.aggregate()

        assertThat(clearStatsSlot.captured).isEmpty()
    }

    @Test
    fun `aggregate groups clear stats by team and start position separately`() = runTest {
        every { jungleMatchRecordJpaService.findDistinctPatchVersions() } returns listOf("16.6")
        every { jungleMatchRecordJpaService.findByPatchVersion("16.6") } returns listOf(
            createRecord(1, "KR_1", "LeeSin", "Graves", true, "16.6", startPosition = "RED_BUFF", team = "BLUE"),
            createRecord(2, "KR_2", "LeeSin", "Nidalee", true, "16.6", startPosition = "BLUE_BUFF", team = "BLUE"),
            createRecord(3, "KR_3", "LeeSin", "Graves", false, "16.6", startPosition = "RED_BUFF", team = "RED")
        )
        every { pureFullcampClearRecordJpaService.findAll() } returns listOf(
            PureFullcampClearRecord(id = 1, matchRecordId = 1, clearTimeMs = 200000),
            PureFullcampClearRecord(id = 2, matchRecordId = 2, clearTimeMs = 210000),
            PureFullcampClearRecord(id = 3, matchRecordId = 3, clearTimeMs = 195000)
        )

        val clearStatsSlot = slot<List<ChampionClearStats>>()
        every { statsJpaService.replaceChampionClearStats("16.6", capture(clearStatsSlot)) } just Runs

        aggregator.aggregate()

        val clearStats = clearStatsSlot.captured
        assertThat(clearStats).hasSize(3)

        val blueRedBuff = clearStats.find { it.team == "BLUE" && it.startPosition == "RED_BUFF" }!!
        assertThat(blueRedBuff.startCount).isEqualTo(1)
        assertThat(blueRedBuff.totalGames).isEqualTo(2)

        val blueBlueBuff = clearStats.find { it.team == "BLUE" && it.startPosition == "BLUE_BUFF" }!!
        assertThat(blueBlueBuff.startCount).isEqualTo(1)
        assertThat(blueBlueBuff.totalGames).isEqualTo(2)

        val redRedBuff = clearStats.find { it.team == "RED" && it.startPosition == "RED_BUFF" }!!
        assertThat(redRedBuff.startCount).isEqualTo(1)
        assertThat(redRedBuff.totalGames).isEqualTo(1)
    }

    private fun createRecord(
        id: Long = 0,
        matchId: String,
        championId: String,
        opponentId: String,
        win: Boolean,
        patchVersion: String,
        bannedChampions: String = "[]",
        startPosition: String? = null,
        team: String = "BLUE"
    ): JungleMatchRecord {
        val record = JungleMatchRecord(
            matchId = matchId,
            championId = championId,
            team = team,
            win = win,
            opponentChampionId = opponentId,
            runes = "{}",
            summonerSpells = "11,4",
            bannedChampions = bannedChampions,
            patchVersion = patchVersion,
            summonerName = "TestPlayer",
            summonerTag = "KR1",
            gameStartedAt = Instant.now()
        )
        record.startPosition = startPosition
        val idField = JungleMatchRecord::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(record, id)
        return record
    }
}
