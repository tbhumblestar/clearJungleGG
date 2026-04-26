package backend.riot.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchTimelineDto(
    val metadata: TimelineMetadataDto,
    val info: TimelineInfoDto
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineMetadataDto(
    val matchId: String,
    val participants: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineInfoDto(
    val frameInterval: Long = 60000,
    val frames: List<TimelineFrameDto> = emptyList(),
    val participants: List<TimelineParticipantMappingDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineFrameDto(
    val timestamp: Long = 0,
    val events: List<TimelineEventDto> = emptyList(),
    val participantFrames: Map<String, ParticipantFrameDto> = emptyMap()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineEventDto(
    val type: String = "",
    val timestamp: Long = 0,
    val participantId: Int? = null,
    val level: Int? = null,
    val killerId: Int? = null,
    val victimId: Int? = null,
    val assistingParticipantIds: List<Int>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ParticipantFrameDto(
    val participantId: Int = 0,
    val position: PositionDto? = null,
    val level: Int = 0,
    val minionsKilled: Int = 0,
    val damageStats: DamageStatsDto? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PositionDto(
    val x: Int = 0,
    val y: Int = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DamageStatsDto(
    val totalDamageDoneToChampions: Long = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineParticipantMappingDto(
    val participantId: Int = 0,
    val puuid: String = ""
)
