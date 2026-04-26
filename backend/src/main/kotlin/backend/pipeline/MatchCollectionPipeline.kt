package backend.pipeline

import backend.jpa.ChampionJpaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

@Component
class MatchCollectionPipeline(
    private val championSyncService: ChampionSyncService,
    private val userPoolCollector: UserPoolCollector,
    private val matchIdCollector: MatchIdCollector,
    private val matchDetailProcessor: MatchDetailProcessor,
    private val timelineAnalyzer: TimelineAnalyzer,
    private val statsAggregator: StatsAggregator,
    private val championJpaService: ChampionJpaService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class PipelineResult(
        val championSync: ChampionSyncService.SyncResult,
        val userPoolSize: Int,
        val uniqueMatchIds: Int,
        val processResult: MatchDetailProcessor.ProcessResult,
        val timelineResult: TimelineAnalyzer.AnalysisResult,
        val statsResult: StatsAggregator.AggregationResult
    )

    suspend fun execute(targetDate: LocalDate, userLimit: Int? = null): PipelineResult {
        val pipelineStart = System.currentTimeMillis()
        log.info("========================================")
        log.info("  Pipeline started for $targetDate")
        log.info("========================================")

        // Step 0: 챔피언 동기화
        var stepStart = System.currentTimeMillis()
        log.info("[Step0:챔피언 동기화] 시작...")
        val syncResult = championSyncService.sync()
        log.info("[Step0:챔피언 동기화] 완료 — ${elapsedSec(stepStart)}초 소요")

        val allChampions = withContext(Dispatchers.IO) { championJpaService.findAll() }
        val championKeyToIdMap = allChampions.associate { it.key to it.id }
        val championNameNormalizer = allChampions.associate { it.id.lowercase() to it.id }

        // Step 1: 유저 풀 수집
        stepStart = System.currentTimeMillis()
        log.info("[Step1:유저 풀 수집] 시작...")
        val fullUserPool = userPoolCollector.collect()
        val userPool = if (userLimit != null) fullUserPool.take(userLimit) else fullUserPool
        if (userLimit != null) log.info("[Step1:유저 풀 수집] limit=$userLimit 적용 (전체 ${fullUserPool.size}명 중 ${userPool.size}명)")
        val puuidToTier = userPool.associate { it.puuid to it.tier }
        log.info("[Step1:유저 풀 수집] 완료 — ${elapsedSec(stepStart)}초 소요")

        // Step 2: 매치 ID 수집
        stepStart = System.currentTimeMillis()
        log.info("[Step2:매치 ID 수집] 시작 — ${userPool.size}명 대상")
        val kst = ZoneId.of("Asia/Seoul")
        val startOfDay = targetDate.atStartOfDay(kst).toEpochSecond()
        val endOfDay = targetDate.plusDays(1).atStartOfDay(kst).toEpochSecond() - 1
        val matchIds = matchIdCollector.collect(userPool.map { it.puuid }, startOfDay, endOfDay)
        log.info("[Step2:매치 ID 수집] 완료 — ${elapsedSec(stepStart)}초 소요, 신규 ${matchIds.size}경기")

        // Step 3: 매치 상세 처리
        stepStart = System.currentTimeMillis()
        log.info("[Step3:매치 상세 처리] 시작 — ${matchIds.size}경기 대상")
        val processResult = matchDetailProcessor.process(matchIds, puuidToTier, championKeyToIdMap, championNameNormalizer)
        log.info("[Step3:매치 상세 처리] 완료 — ${elapsedSec(stepStart)}초 소요")

        // Step 4: 타임라인 분석
        stepStart = System.currentTimeMillis()
        log.info("[Step4:타임라인 분석] 시작 — ${processResult.junglersByMatch.size}경기 대상")
        val timelineResult = timelineAnalyzer.analyze(processResult.junglersByMatch)
        log.info("[Step4:타임라인 분석] 완료 — ${elapsedSec(stepStart)}초 소요")

        // Step 5: 통계 집계
        stepStart = System.currentTimeMillis()
        log.info("[Step5:통계 집계] 시작...")
        val statsResult = statsAggregator.aggregate()
        log.info("[Step5:통계 집계] 완료 — ${elapsedSec(stepStart)}초 소요")

        val totalElapsed = elapsedSec(pipelineStart)
        log.info("========================================")
        log.info("  파이프라인 완료 — 총 ${totalElapsed}초")
        log.info("  매치 기록: ${processResult.savedCount}건")
        log.info("  클리어 기록: ${timelineResult.clearRecordsCreated}건")
        log.info("  챔피언 통계: ${statsResult.statsCount}건")
        log.info("  상성 데이터: ${statsResult.matchupCount}건")
        log.info("  클리어 통계: ${statsResult.clearStatsCount}건")
        log.info("========================================")

        return PipelineResult(
            championSync = syncResult,
            userPoolSize = userPool.size,
            uniqueMatchIds = matchIds.size,
            processResult = processResult,
            timelineResult = timelineResult,
            statsResult = statsResult
        )
    }

    private fun elapsedSec(startMs: Long): Long = (System.currentTimeMillis() - startMs) / 1000
}
