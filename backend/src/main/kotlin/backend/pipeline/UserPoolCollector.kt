package backend.pipeline

import backend.riot.RiotApiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserPoolCollector(private val riotApiClient: RiotApiClient) {

    private val log = LoggerFactory.getLogger(javaClass)

    data class UserPoolEntry(val puuid: String, val tier: String)

    suspend fun collect(): List<UserPoolEntry> {
        val challenger = riotApiClient.getChallengerLeague()
        val grandmaster = riotApiClient.getGrandmasterLeague()

        val pool = challenger.entries.map { UserPoolEntry(it.puuid, "CHALLENGER") } +
                grandmaster.entries.map { UserPoolEntry(it.puuid, "GRANDMASTER") }

        log.info("[Step1:유저 풀 수집] 완료 — Challenger: ${challenger.entries.size}, Grandmaster: ${grandmaster.entries.size}, Total: ${pool.size}")
        return pool
    }
}
