package backend.riot.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import tools.jackson.databind.JsonNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchDetailDto(
    val metadata: MatchMetadataDto,
    val info: MatchInfoDto
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchMetadataDto(
    val matchId: String,
    val participants: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MatchInfoDto(
    val gameVersion: String,
    val gameDuration: Long,
    val gameStartTimestamp: Long,
    val participants: List<ParticipantDto> = emptyList(),
    val teams: List<TeamDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ParticipantDto(
    val puuid: String,
    val riotIdGameName: String? = null,
    val riotIdTagline: String? = null,
    val championName: String,
    val teamId: Int,
    val teamPosition: String = "",
    val win: Boolean,
    val perks: JsonNode? = null,
    val summoner1Id: Int = 0,
    val summoner2Id: Int = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TeamDto(
    val teamId: Int,
    val bans: List<BanDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BanDto(
    val championId: Int,
    val pickTurn: Int = 0
)
