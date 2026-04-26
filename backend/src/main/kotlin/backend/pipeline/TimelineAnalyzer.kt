package backend.pipeline

import backend.domain.match.PureFullcampClearRecord
import backend.jpa.PureFullcampClearRecordJpaService
import backend.jpa.JungleMatchRecordJpaService
import backend.riot.RiotApiClient
import backend.riot.dto.MatchTimelineDto
import backend.riot.dto.TimelineFrameDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class TimelineAnalyzer(
    private val riotApiClient: RiotApiClient,
    private val jungleMatchRecordJpaService: JungleMatchRecordJpaService,
    private val pureFullcampClearRecordJpaService: PureFullcampClearRecordJpaService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class AnalysisResult(
        val analyzed: Int,
        val clearRecordsCreated: Int,
        val purityFailed: Int,
        val errors: Int
    )

    suspend fun analyze(junglersByMatch: Map<String, List<MatchDetailProcessor.JunglerMapping>>): AnalysisResult {
        var analyzed = 0
        var clearRecordsCreated = 0
        var purityFailed = 0
        var errors = 0
        val clearBatch = mutableListOf<PureFullcampClearRecord>()
        val startPositionUpdates = mutableListOf<Pair<Long, String>>() // matchRecordId -> startPosition

        log.info("[Step4:타임라인 분석] 시작 — ${junglersByMatch.size}경기")

        junglersByMatch.entries.forEachIndexed { index, (matchId, junglers) ->
            try {
                val timeline = riotApiClient.getMatchTimeline(matchId)
                val puuidToParticipantId = buildPuuidToParticipantIdMap(timeline)

                for (jungler in junglers) {
                    val participantId = puuidToParticipantId[jungler.puuid] ?: continue

                    val startPosition = determineStartPosition(timeline, participantId, jungler.team)
                    startPositionUpdates.add(jungler.matchRecordId to startPosition)

                    val level4Timestamp = findLevel4Timestamp(timeline, participantId) ?: continue

                    if (checkPurity(timeline, participantId, level4Timestamp)) {
                        clearBatch.add(PureFullcampClearRecord(matchRecordId = jungler.matchRecordId, clearTimeMs = level4Timestamp.toInt()))
                        clearRecordsCreated++
                    } else {
                        purityFailed++
                    }
                }
                analyzed++
            } catch (e: WebClientResponseException) {
                log.warn("Failed to fetch timeline for match: $matchId, status: ${e.statusCode}")
                errors++
            } catch (e: Exception) {
                log.error("Error analyzing timeline for match: $matchId", e)
                errors++
            }

            if ((index + 1) % 100 == 0) {
                val remaining = (junglersByMatch.size - index - 1) * 1.3
                log.info("[Step4:타임라인 분석] ${index + 1}/${junglersByMatch.size}경기 (클리어기록: $clearRecordsCreated, 퓨어실패: $purityFailed, 에러: $errors, 남은시간 ~${remaining.toInt()}초)")
            }
        }

        // DB 저장
        withContext(Dispatchers.IO) {
            if (startPositionUpdates.isNotEmpty()) {
                updateStartPositions(startPositionUpdates)
            }
            if (clearBatch.isNotEmpty()) {
                pureFullcampClearRecordJpaService.saveAll(clearBatch)
            }
        }

        log.info("[Step4:타임라인 분석] 완료 — 분석: $analyzed, 클리어기록: $clearRecordsCreated, 퓨어실패: $purityFailed, 에러: $errors")
        return AnalysisResult(analyzed, clearRecordsCreated, purityFailed, errors)
    }

    private fun updateStartPositions(updates: List<Pair<Long, String>>) {
        val ids = updates.map { it.first }
        val records = jungleMatchRecordJpaService.findAllByIds(ids)
        val positionMap = updates.toMap()
        records.forEach { record ->
            positionMap[record.id]?.let { record.startPosition = it }
        }
        jungleMatchRecordJpaService.saveAll(records)
    }

    private fun buildPuuidToParticipantIdMap(timeline: MatchTimelineDto): Map<String, Int> {
        if (timeline.info.participants.isNotEmpty()) {
            return timeline.info.participants.associate { it.puuid to it.participantId }
        }
        return timeline.metadata.participants.mapIndexed { index, puuid -> puuid to (index + 1) }.toMap()
    }

    fun determineStartPosition(timeline: MatchTimelineDto, participantId: Int, team: String): String {
        val frame = timeline.info.frames.getOrNull(1) ?: return "UNKNOWN"
        val pf = frame.participantFrames[participantId.toString()] ?: return "UNKNOWN"
        val pos = pf.position ?: return "UNKNOWN"
        return classifyCamp(pos.x, pos.y, team)
    }

    fun classifyCamp(x: Int, y: Int, team: String): String {
        val camps = if (team == "BLUE") {
            listOf(
                Camp("BLUE_BUFF", 3821, 7901),
                Camp("RED_BUFF", 7462, 3934),
                Camp("RAPTORS", 6974, 5426),
                Camp("WOLVES", 3782, 6443),
                Camp("KRUGS", 8350, 2834),
                Camp("GROMP", 2175, 8348)
            )
        } else {
            listOf(
                Camp("BLUE_BUFF", 10931, 7071),
                Camp("RED_BUFF", 7462, 10834),
                Camp("RAPTORS", 7852, 9382),
                Camp("WOLVES", 11008, 8420),
                Camp("KRUGS", 6476, 12006),
                Camp("GROMP", 12603, 6438)
            )
        }

        return camps.minByOrNull { camp ->
            val dx = (x - camp.x).toDouble()
            val dy = (y - camp.y).toDouble()
            dx * dx + dy * dy
        }?.name ?: "UNKNOWN"
    }

    fun findLevel4Timestamp(timeline: MatchTimelineDto, participantId: Int): Long? {
        for (frame in timeline.info.frames) {
            for (event in frame.events) {
                if (event.type == "LEVEL_UP" && event.participantId == participantId && event.level == 4) {
                    return event.timestamp
                }
            }
        }
        return null
    }

    fun checkPurity(timeline: MatchTimelineDto, participantId: Int, level4Timestamp: Long): Boolean {
        // Check 1: 킬/데스/어시스트 관여
        for (frame in timeline.info.frames) {
            for (event in frame.events) {
                if (event.timestamp >= level4Timestamp) break
                if (event.type == "CHAMPION_KILL") {
                    if (event.killerId == participantId) return false
                    if (event.victimId == participantId) return false
                    if (event.assistingParticipantIds?.contains(participantId) == true) return false
                }
            }
        }

        // Check 2 & 3: 4레벨 달성 직전 프레임
        val frameBeforeLevel4 = findFrameBeforeTimestamp(timeline, level4Timestamp)
        if (frameBeforeLevel4 != null) {
            val pf = frameBeforeLevel4.participantFrames[participantId.toString()]
            if (pf != null) {
                if (pf.minionsKilled > 0) return false
                if ((pf.damageStats?.totalDamageDoneToChampions ?: 0) > 0) return false
            }
        }

        return true
    }

    private fun findFrameBeforeTimestamp(timeline: MatchTimelineDto, timestamp: Long): TimelineFrameDto? {
        var lastFrame: TimelineFrameDto? = null
        for (frame in timeline.info.frames) {
            if (frame.timestamp >= timestamp) return lastFrame
            lastFrame = frame
        }
        return lastFrame
    }

    private data class Camp(val name: String, val x: Int, val y: Int)
}
