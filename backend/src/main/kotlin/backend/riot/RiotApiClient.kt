package backend.riot

import backend.riot.dto.ChampionDataDragonDto
import backend.riot.dto.LeagueListDto
import backend.riot.dto.MatchDetailDto
import backend.riot.dto.MatchTimelineDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class RiotApiClient(
    @Qualifier("krWebClient") private val krWebClient: WebClient,
    @Qualifier("asiaWebClient") private val asiaWebClient: WebClient,
    @Qualifier("ddragonWebClient") private val ddragonWebClient: WebClient,
    private val rateLimiter: RiotRateLimiter
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun getChallengerLeague(): LeagueListDto =
        callWithRetry {
            krWebClient.get()
                .uri("/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5")
                .retrieve()
                .bodyToMono(LeagueListDto::class.java)
                .awaitSingle()
        }

    suspend fun getGrandmasterLeague(): LeagueListDto =
        callWithRetry {
            krWebClient.get()
                .uri("/lol/league/v4/grandmasterleagues/by-queue/RANKED_SOLO_5x5")
                .retrieve()
                .bodyToMono(LeagueListDto::class.java)
                .awaitSingle()
        }

    suspend fun getMatchIds(
        puuid: String,
        startTime: Long,
        endTime: Long,
        queue: Int = 420,
        count: Int = 100
    ): List<String> =
        callWithRetry {
            asiaWebClient.get()
                .uri { builder ->
                    builder.path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
                        .queryParam("startTime", startTime)
                        .queryParam("endTime", endTime)
                        .queryParam("queue", queue)
                        .queryParam("count", count)
                        .build(puuid)
                }
                .retrieve()
                .bodyToMono(Array<String>::class.java)
                .map { it.toList() }
                .awaitSingle()
        }

    suspend fun getMatchDetail(matchId: String): MatchDetailDto =
        callWithRetry {
            asiaWebClient.get()
                .uri("/lol/match/v5/matches/{matchId}", matchId)
                .retrieve()
                .bodyToMono(MatchDetailDto::class.java)
                .awaitSingle()
        }

    suspend fun getMatchTimeline(matchId: String): MatchTimelineDto =
        callWithRetry {
            asiaWebClient.get()
                .uri("/lol/match/v5/matches/{matchId}/timeline", matchId)
                .retrieve()
                .bodyToMono(MatchTimelineDto::class.java)
                .awaitSingle()
        }

    suspend fun getLatestVersion(): String =
        ddragonWebClient.get()
            .uri("/api/versions.json")
            .retrieve()
            .bodyToMono(Array<String>::class.java)
            .map { it.first() }
            .awaitSingle()

    suspend fun getChampionData(version: String): ChampionDataDragonDto =
        ddragonWebClient.get()
            .uri("/cdn/{version}/data/ko_KR/champion.json", version)
            .retrieve()
            .bodyToMono(ChampionDataDragonDto::class.java)
            .awaitSingle()

    private suspend fun <T> callWithRetry(maxRetries: Int = 3, block: suspend () -> T): T {
        repeat(maxRetries) { attempt ->
            try {
                return rateLimiter.withRateLimit { block() }
            } catch (e: WebClientResponseException) {
                if (e.statusCode.value() == 429) {
                    val retryAfter = e.headers.getFirst("Retry-After")?.toLongOrNull() ?: 10
                    log.warn("Rate limited (429). Retry-After: ${retryAfter}s, attempt: ${attempt + 1}")
                    delay(retryAfter * 1000)
                } else {
                    throw e
                }
            }
        }
        return rateLimiter.withRateLimit { block() }
    }
}
