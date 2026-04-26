package backend.pipeline

import backend.jpa.JungleMatchRecordJpaService
import backend.riot.RiotApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class MatchIdCollector(
    private val riotApiClient: RiotApiClient,
    private val jungleMatchRecordJpaService: JungleMatchRecordJpaService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun collect(
        puuids: List<String>,
        startTime: Long,
        endTime: Long
    ): Set<String> {
        val allMatchIds = mutableSetOf<String>()
        var errorCount = 0

        log.info("[Step2:매치 ID 수집] 시작 — ${puuids.size}명")

        puuids.forEachIndexed { index, puuid ->
            try {
                val matchIds = riotApiClient.getMatchIds(puuid, startTime, endTime)
                allMatchIds.addAll(matchIds)
            } catch (e: WebClientResponseException) {
                if (e.statusCode.value() == 404) {
                    log.debug("No matches found for puuid: ${puuid.take(8)}...")
                } else {
                    log.warn("Failed to get matches for puuid: ${puuid.take(8)}..., status: ${e.statusCode}")
                    errorCount++
                }
            }

            if ((index + 1) % 100 == 0) {
                val elapsed = (index + 1) * 1.3
                val remaining = (puuids.size - index - 1) * 1.3
                log.info("[Step2:매치 ID 수집] ${index + 1}/${puuids.size}명 완료 (${allMatchIds.size}경기 발견, 남은시간 ~${remaining.toInt()}초)")
            }
        }

        val existingMatchIds = withContext(Dispatchers.IO) {
            if (allMatchIds.isEmpty()) emptySet()
            else allMatchIds.chunked(1000).flatMap { chunk ->
                jungleMatchRecordJpaService.findExistingMatchIds(chunk)
            }.toSet()
        }

        val newMatchIds = allMatchIds - existingMatchIds
        log.info("[Step2:매치 ID 수집] 완료 — 전체: ${allMatchIds.size}, 기존: ${existingMatchIds.size}, 신규: ${newMatchIds.size}, 에러: $errorCount")
        return newMatchIds
    }
}
