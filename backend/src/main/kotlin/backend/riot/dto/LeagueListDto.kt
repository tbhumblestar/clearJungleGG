package backend.riot.dto

data class LeagueListDto(
    val tier: String,
    val entries: List<LeagueEntryDto>
)

data class LeagueEntryDto(
    val puuid: String,
    val leaguePoints: Int = 0
)
