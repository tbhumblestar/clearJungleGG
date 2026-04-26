package backend.pipeline

import backend.riot.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimelineAnalyzerTest {

    private val analyzer = TimelineAnalyzer(
        riotApiClient = io.mockk.mockk(),
        jungleMatchRecordJpaService = io.mockk.mockk(),
        pureFullcampClearRecordJpaService = io.mockk.mockk()
    )

    @Test
    fun `classifyCamp returns BLUE_BUFF for blue side near blue buff coordinates`() {
        val result = analyzer.classifyCamp(3800, 7900, "BLUE")
        assertThat(result).isEqualTo("BLUE_BUFF")
    }

    @Test
    fun `classifyCamp returns RED_BUFF for blue side near red buff coordinates`() {
        val result = analyzer.classifyCamp(7500, 3900, "BLUE")
        assertThat(result).isEqualTo("RED_BUFF")
    }

    @Test
    fun `classifyCamp returns RED_BUFF for red side near red buff coordinates`() {
        val result = analyzer.classifyCamp(7500, 10800, "RED")
        assertThat(result).isEqualTo("RED_BUFF")
    }

    @Test
    fun `findLevel4Timestamp extracts correct timestamp`() {
        val timeline = createTimeline(
            events = listOf(
                TimelineEventDto(type = "LEVEL_UP", timestamp = 120000, participantId = 1, level = 2),
                TimelineEventDto(type = "LEVEL_UP", timestamp = 150000, participantId = 1, level = 3),
                TimelineEventDto(type = "LEVEL_UP", timestamp = 185000, participantId = 1, level = 4)
            )
        )

        val result = analyzer.findLevel4Timestamp(timeline, 1)
        assertThat(result).isEqualTo(185000L)
    }

    @Test
    fun `findLevel4Timestamp returns null when level 4 not reached`() {
        val timeline = createTimeline(
            events = listOf(
                TimelineEventDto(type = "LEVEL_UP", timestamp = 120000, participantId = 1, level = 2),
                TimelineEventDto(type = "LEVEL_UP", timestamp = 150000, participantId = 1, level = 3)
            )
        )

        val result = analyzer.findLevel4Timestamp(timeline, 1)
        assertThat(result).isNull()
    }

    @Test
    fun `checkPurity returns true for clean clear`() {
        val timeline = createTimelineWithFrames(
            events = listOf(
                TimelineEventDto(type = "LEVEL_UP", timestamp = 185000, participantId = 1, level = 4)
            ),
            participantFrames = mapOf("1" to ParticipantFrameDto(
                participantId = 1, minionsKilled = 0,
                damageStats = DamageStatsDto(totalDamageDoneToChampions = 0)
            ))
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isTrue()
    }

    @Test
    fun `checkPurity fails when involved in champion kill before level 4`() {
        val timeline = createTimeline(
            events = listOf(
                TimelineEventDto(type = "CHAMPION_KILL", timestamp = 100000, killerId = 1, victimId = 5)
            )
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isFalse()
    }

    @Test
    fun `checkPurity fails when died before level 4`() {
        val timeline = createTimeline(
            events = listOf(
                TimelineEventDto(type = "CHAMPION_KILL", timestamp = 100000, killerId = 5, victimId = 1)
            )
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isFalse()
    }

    @Test
    fun `checkPurity fails when assisted kill before level 4`() {
        val timeline = createTimeline(
            events = listOf(
                TimelineEventDto(type = "CHAMPION_KILL", timestamp = 100000, killerId = 3, victimId = 5, assistingParticipantIds = listOf(1))
            )
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isFalse()
    }

    @Test
    fun `checkPurity fails when minions killed before level 4`() {
        val timeline = createTimelineWithFrames(
            events = emptyList(),
            participantFrames = mapOf("1" to ParticipantFrameDto(
                participantId = 1, minionsKilled = 3,
                damageStats = DamageStatsDto(totalDamageDoneToChampions = 0)
            ))
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isFalse()
    }

    @Test
    fun `checkPurity fails when champion damage dealt before level 4`() {
        val timeline = createTimelineWithFrames(
            events = emptyList(),
            participantFrames = mapOf("1" to ParticipantFrameDto(
                participantId = 1, minionsKilled = 0,
                damageStats = DamageStatsDto(totalDamageDoneToChampions = 150)
            ))
        )

        assertThat(analyzer.checkPurity(timeline, 1, 185000)).isFalse()
    }

    private fun createTimeline(events: List<TimelineEventDto>): MatchTimelineDto =
        MatchTimelineDto(
            metadata = TimelineMetadataDto(matchId = "KR_1"),
            info = TimelineInfoDto(
                frames = listOf(
                    TimelineFrameDto(timestamp = 0, events = events, participantFrames = emptyMap())
                )
            )
        )

    private fun createTimelineWithFrames(
        events: List<TimelineEventDto>,
        participantFrames: Map<String, ParticipantFrameDto>
    ): MatchTimelineDto =
        MatchTimelineDto(
            metadata = TimelineMetadataDto(matchId = "KR_1"),
            info = TimelineInfoDto(
                frames = listOf(
                    TimelineFrameDto(timestamp = 0, events = emptyList(), participantFrames = emptyMap()),
                    TimelineFrameDto(timestamp = 60000, events = events, participantFrames = participantFrames),
                    TimelineFrameDto(timestamp = 120000, events = emptyList(), participantFrames = participantFrames),
                    TimelineFrameDto(timestamp = 200000, events = emptyList(), participantFrames = emptyMap())
                )
            )
        )
}
