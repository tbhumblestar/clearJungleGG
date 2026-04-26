package backend.pipeline

import backend.domain.stats.ChampionClearStats
import backend.domain.stats.ChampionMatchup
import backend.domain.stats.ChampionStats
import backend.jpa.JungleMatchRecordJpaService
import backend.jpa.PureFullcampClearRecordJpaService
import backend.jpa.StatsJpaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import kotlin.math.roundToInt

@Component
class StatsAggregator(
    private val jungleMatchRecordJpaService: JungleMatchRecordJpaService,
    private val pureFullcampClearRecordJpaService: PureFullcampClearRecordJpaService,
    private val statsJpaService: StatsJpaService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class AggregationResult(val patchVersions: List<String>, val statsCount: Int, val matchupCount: Int, val clearStatsCount: Int)

    suspend fun aggregate(): AggregationResult {
        val patchVersions = withContext(Dispatchers.IO) {
            jungleMatchRecordJpaService.findDistinctPatchVersions()
        }

        var totalStats = 0
        var totalMatchups = 0
        var totalClearStats = 0

        log.info("[Step5:통계 집계] 시작 — 패치버전: $patchVersions")

        val allClearRecords = withContext(Dispatchers.IO) {
            pureFullcampClearRecordJpaService.findAll()
        }
        val clearRecordsByMatchRecordId = allClearRecords.associateBy { it.matchRecordId }

        for (patchVersion in patchVersions) {
            val records = withContext(Dispatchers.IO) {
                jungleMatchRecordJpaService.findByPatchVersion(patchVersion)
            }

            if (records.isEmpty()) continue

            // champion_stats 집계
            val stats = aggregateChampionStats(records, patchVersion)
            withContext(Dispatchers.IO) {
                statsJpaService.replaceChampionStats(patchVersion, stats)
            }
            totalStats += stats.size

            // champion_matchup 집계
            val matchups = aggregateChampionMatchups(records, patchVersion)
            withContext(Dispatchers.IO) {
                statsJpaService.replaceChampionMatchups(patchVersion, matchups)
            }
            totalMatchups += matchups.size

            // champion_clear_stats 집계
            val clearStats = aggregateChampionClearStats(records, clearRecordsByMatchRecordId, patchVersion)
            withContext(Dispatchers.IO) {
                statsJpaService.replaceChampionClearStats(patchVersion, clearStats)
            }
            totalClearStats += clearStats.size

            log.info("[Step5:통계 집계] 패치 $patchVersion — 챔피언통계: ${stats.size}, 상성: ${matchups.size}, 클리어통계: ${clearStats.size}")
        }

        log.info("[Step5:통계 집계] 완료 — 패치: ${patchVersions.size}개, 챔피언통계: $totalStats, 상성: $totalMatchups, 클리어통계: $totalClearStats")
        return AggregationResult(patchVersions, totalStats, totalMatchups, totalClearStats)
    }

    private fun aggregateChampionStats(records: List<backend.domain.match.JungleMatchRecord>, patchVersion: String): List<ChampionStats> {
        val totalMatches = records.map { it.matchId }.distinct().size
        val now = Instant.now()

        // 픽 수, 승리 수
        val pickWinMap = records.groupBy { it.championId }.mapValues { (_, recs) ->
            recs.size to recs.count { it.win }
        }

        // 밴 수 (match_id 중복 제거)
        val banCountMap = mutableMapOf<String, Int>()
        val processedMatchIds = mutableSetOf<String>()
        for (record in records) {
            if (processedMatchIds.contains(record.matchId)) continue
            processedMatchIds.add(record.matchId)
            try {
                val banned = objectMapper.readValue(record.bannedChampions, List::class.java)
                for (championId in banned) {
                    val id = championId.toString()
                    banCountMap[id] = (banCountMap[id] ?: 0) + 1
                }
            } catch (_: Exception) { }
        }

        val allChampionIds = pickWinMap.keys + banCountMap.keys
        return allChampionIds.map { championId ->
            val (pickCount, winCount) = pickWinMap[championId] ?: (0 to 0)
            ChampionStats(
                championId = championId,
                patchVersion = patchVersion,
                pickCount = pickCount,
                winCount = winCount,
                banCount = banCountMap[championId] ?: 0,
                totalMatches = totalMatches,
                updatedAt = now
            )
        }
    }

    private fun aggregateChampionClearStats(
        records: List<backend.domain.match.JungleMatchRecord>,
        clearRecordsByMatchRecordId: Map<Long, backend.domain.match.PureFullcampClearRecord>,
        patchVersion: String
    ): List<ChampionClearStats> {
        val now = Instant.now()

        val recordsWithStartPosition = records.filter { it.startPosition != null }

        // 시작 위치 비율 계산 (전체 경기 기반)
        val totalGamesByChampionTeam = recordsWithStartPosition
            .groupBy { it.championId to it.team }
            .mapValues { (_, recs) -> recs.size }

        val startCountMap = recordsWithStartPosition
            .groupBy { Triple(it.championId, it.team, it.startPosition!!) }
            .mapValues { (_, recs) -> recs.size }

        // 클리어 타임 집계 (순수 풀캠프 기록 기반)
        val clearDataByKey = recordsWithStartPosition
            .mapNotNull { record ->
                clearRecordsByMatchRecordId[record.id]?.let { clearRecord -> record to clearRecord }
            }
            .groupBy { (record, _) -> Triple(record.championId, record.team, record.startPosition!!) }

        // 시작 위치가 존재하는 모든 조합에 대해 통계 생성
        return startCountMap.map { (key, startCount) ->
            val (championId, team, startPosition) = key
            val totalGames = totalGamesByChampionTeam[championId to team] ?: 0
            val clearPairs = clearDataByKey[key]
            val clearTimes = clearPairs?.map { (_, clearRecord) -> clearRecord.clearTimeMs }

            ChampionClearStats(
                championId = championId,
                patchVersion = patchVersion,
                team = team,
                startPosition = startPosition,
                avgClearTimeMs = clearTimes?.average()?.roundToInt() ?: 0,
                bestClearTimeMs = clearTimes?.min() ?: 0,
                sampleCount = clearTimes?.size ?: 0,
                startCount = startCount,
                totalGames = totalGames,
                updatedAt = now
            )
        }
    }

    private fun aggregateChampionMatchups(records: List<backend.domain.match.JungleMatchRecord>, patchVersion: String): List<ChampionMatchup> {
        val now = Instant.now()

        return records.groupBy { it.championId to it.opponentChampionId }
            .map { (key, recs) ->
                val (championId, opponentId) = key
                ChampionMatchup(
                    championId = championId,
                    opponentChampionId = opponentId,
                    patchVersion = patchVersion,
                    wins = recs.count { it.win },
                    losses = recs.count { !it.win },
                    updatedAt = now
                )
            }
    }
}
