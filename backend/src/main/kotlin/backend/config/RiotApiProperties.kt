package backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "riot")
data class RiotApiProperties(
    val apiKey: String,
    val krBaseUrl: String = "https://kr.api.riotgames.com",
    val asiaBaseUrl: String = "https://asia.api.riotgames.com",
    val ddragonBaseUrl: String = "https://ddragon.leagueoflegends.com"
)
