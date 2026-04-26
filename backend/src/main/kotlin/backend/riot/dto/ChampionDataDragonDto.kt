package backend.riot.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionDataDragonDto(
    val version: String = "",
    val data: Map<String, ChampionDragonEntryDto> = emptyMap()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChampionDragonEntryDto(
    val id: String,
    val key: String,
    val name: String,
    val title: String
)
