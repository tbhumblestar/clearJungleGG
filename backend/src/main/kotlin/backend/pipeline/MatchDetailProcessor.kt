package backend.pipeline

import backend.domain.match.JungleMatchRecord
import backend.jpa.JungleMatchRecordJpaService
import backend.riot.RiotApiClient
import backend.riot.dto.MatchDetailDto
import backend.riot.dto.ParticipantDto
import tools.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Instant

@Component
class MatchDetailProcessor(
    private val riotApiClient: RiotApiClient,
    private val jungleMatchRecordJpaService: JungleMatchRecordJpaService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REMAKE_DURATION_THRESHOLD = 300L
        private const val SAVE_BATCH_SIZE = 50
    }

    data class JunglerMapping(val matchRecordId: Long, val puuid: String, val team: String)

    data class ProcessResult(
        val savedCount: Int,
        val skippedRemake: Int,
        val skippedNoJungler: Int,
        val errors: Int,
        val junglersByMatch: Map<String, List<JunglerMapping>>
    )

    suspend fun process(
        matchIds: Set<String>,
        userPool: Map<String, String>,
        championKeyToIdMap: Map<Int, String>,
        championNameNormalizer: Map<String, String> = emptyMap()
    ): ProcessResult {
        var savedCount = 0
        var skippedRemake = 0
        var skippedNoJungler = 0
        var errors = 0
        val batch = mutableListOf<Pair<JungleMatchRecord, String>>() // record + puuid
        val allJunglersByMatch = mutableMapOf<String, List<JunglerMapping>>()

        log.info("[Step3:매치 상세 처리] 시작 — ${matchIds.size}경기")

        matchIds.forEachIndexed { index, matchId ->
            try {
                val detail = riotApiClient.getMatchDetail(matchId)

                if (detail.info.gameDuration < REMAKE_DURATION_THRESHOLD) {
                    skippedRemake++
                    return@forEachIndexed
                }

                val junglers = detail.info.participants.filter { it.teamPosition == "JUNGLE" }
                if (junglers.size != 2) {
                    skippedNoJungler++
                    return@forEachIndexed
                }

                val recordsWithPuuid = createMatchRecords(detail, junglers, userPool, championKeyToIdMap, championNameNormalizer)
                batch.addAll(recordsWithPuuid)

                if (batch.size >= SAVE_BATCH_SIZE) {
                    val saved = saveBatchAndCollectMappings(batch, allJunglersByMatch)
                    savedCount += saved
                    batch.clear()
                }
            } catch (e: WebClientResponseException) {
                log.warn("Failed to process match: $matchId, status: ${e.statusCode}")
                errors++
            } catch (e: Exception) {
                log.error("Unexpected error processing match: $matchId", e)
                errors++
            }

            if ((index + 1) % 100 == 0) {
                val remaining = (matchIds.size - index - 1) * 1.3
                log.info("[Step3:매치 상세 처리] ${index + 1}/${matchIds.size}경기 (저장: $savedCount, 리메이크: $skippedRemake, 정글러없음: $skippedNoJungler, 에러: $errors, 남은시간 ~${remaining.toInt()}초)")
            }
        }

        if (batch.isNotEmpty()) {
            savedCount += saveBatchAndCollectMappings(batch, allJunglersByMatch)
        }

        log.info("[Step3:매치 상세 처리] 완료 — 저장: $savedCount, 리메이크: $skippedRemake, 정글러없음: $skippedNoJungler, 에러: $errors")
        return ProcessResult(savedCount, skippedRemake, skippedNoJungler, errors, allJunglersByMatch)
    }

    private fun createMatchRecords(
        detail: MatchDetailDto,
        junglers: List<ParticipantDto>,
        userPool: Map<String, String>,
        championKeyToIdMap: Map<Int, String>,
        championNameNormalizer: Map<String, String>
    ): List<Pair<JungleMatchRecord, String>> {
        val matchId = detail.metadata.matchId
        val patchVersion = detail.info.gameVersion.split(".").take(2).joinToString(".")
        val gameStartedAt = Instant.ofEpochMilli(detail.info.gameStartTimestamp)
        val bannedChampions = extractBannedChampions(detail, championKeyToIdMap)
        val bannedJson = objectMapper.writeValueAsString(bannedChampions)

        val jungler1 = junglers[0]
        val jungler2 = junglers[1]

        val normalize = { name: String -> championNameNormalizer[name.lowercase()] ?: name }

        return listOf(
            buildMatchRecord(matchId, jungler1, normalize(jungler2.championName), patchVersion, gameStartedAt, bannedJson, userPool, normalize) to jungler1.puuid,
            buildMatchRecord(matchId, jungler2, normalize(jungler1.championName), patchVersion, gameStartedAt, bannedJson, userPool, normalize) to jungler2.puuid
        )
    }

    private fun buildMatchRecord(
        matchId: String,
        jungler: ParticipantDto,
        opponentChampionId: String,
        patchVersion: String,
        gameStartedAt: Instant,
        bannedChampionsJson: String,
        userPool: Map<String, String>,
        normalize: (String) -> String
    ): JungleMatchRecord {
        val team = if (jungler.teamId == 100) "BLUE" else "RED"
        val runesJson = if (jungler.perks != null) objectMapper.writeValueAsString(jungler.perks) else "{}"

        return JungleMatchRecord(
            matchId = matchId,
            championId = normalize(jungler.championName),
            team = team,
            win = jungler.win,
            opponentChampionId = opponentChampionId,
            runes = runesJson,
            summonerSpells = "${jungler.summoner1Id},${jungler.summoner2Id}",
            bannedChampions = bannedChampionsJson,
            patchVersion = patchVersion,
            summonerName = jungler.riotIdGameName ?: "Unknown",
            summonerTag = jungler.riotIdTagline ?: "KR1",
            summonerTier = userPool[jungler.puuid],
            gameStartedAt = gameStartedAt
        )
    }

    private fun extractBannedChampions(
        detail: MatchDetailDto,
        championKeyToIdMap: Map<Int, String>
    ): List<String> =
        detail.info.teams
            .flatMap { it.bans }
            .map { it.championId }
            .filter { it > 0 }
            .mapNotNull { championKeyToIdMap[it] }

    private suspend fun saveBatchAndCollectMappings(
        batch: List<Pair<JungleMatchRecord, String>>,
        allJunglersByMatch: MutableMap<String, List<JunglerMapping>>
    ): Int =
        withContext(Dispatchers.IO) {
            try {
                val records = batch.map { it.first }
                val puuids = batch.map { it.second }
                val saved = jungleMatchRecordJpaService.saveAll(records)

                saved.forEachIndexed { i, record ->
                    val puuid = puuids[i]
                    val mapping = JunglerMapping(record.id, puuid, record.team)
                    val matchMappings = allJunglersByMatch.getOrDefault(record.matchId, emptyList())
                    allJunglersByMatch[record.matchId] = matchMappings + mapping
                }

                saved.size
            } catch (e: Exception) {
                log.error("Failed to save batch of ${batch.size} records", e)
                0
            }
        }
}
