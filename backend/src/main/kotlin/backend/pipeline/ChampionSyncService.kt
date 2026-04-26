package backend.pipeline

import backend.domain.champion.Champion
import backend.jpa.ChampionJpaService
import backend.riot.RiotApiClient
import tools.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ChampionSyncService(
    private val riotApiClient: RiotApiClient,
    private val championJpaService: ChampionJpaService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class SyncResult(val total: Int, val created: Int, val updated: Int)

    suspend fun sync(): SyncResult {
        val latestVersion = riotApiClient.getLatestVersion()
        val patchVersion = latestVersion.split(".").take(2).joinToString(".")
        log.info("[Step0:챔피언 동기화] DDragon version: $latestVersion, patch: $patchVersion")

        val ddragonData = riotApiClient.getChampionData(latestVersion)

        return withContext(Dispatchers.IO) {
            val existingMap = championJpaService.findAll().associateBy { it.id }
            var created = 0
            var updated = 0

            val champions = ddragonData.data.map { (id, entry) ->
                val existing = existingMap[id]
                if (existing != null) {
                    val changes = detectChanges(existing, entry.name, entry.title)
                    if (changes.isNotEmpty()) {
                        val historyEntry = mapOf(
                            "patch" to patchVersion,
                            "changed_at" to Instant.now().toString(),
                            "changes" to changes
                        )
                        val history = objectMapper.readTree(existing.championHistory).toMutableList()
                        history.add(objectMapper.valueToTree(historyEntry))
                        existing.championHistory = objectMapper.writeValueAsString(history)
                        existing.name = entry.name
                        existing.title = entry.title
                        updated++
                    }
                    existing.patchVersion = patchVersion
                    existing.updatedAt = Instant.now()
                    existing
                } else {
                    created++
                    Champion(
                        id = id,
                        key = entry.key.toInt(),
                        name = entry.name,
                        title = entry.title,
                        patchVersion = patchVersion
                    )
                }
            }

            championJpaService.saveAll(champions)
            log.info("[Step0:챔피언 동기화] 완료 — total: ${champions.size}, created: $created, updated: $updated")
            SyncResult(total = champions.size, created = created, updated = updated)
        }
    }

    private fun detectChanges(existing: Champion, newName: String, newTitle: String): Map<String, Map<String, String>> {
        val changes = mutableMapOf<String, Map<String, String>>()
        if (existing.name != newName) {
            changes["name"] = mapOf("before" to existing.name, "after" to newName)
        }
        if (existing.title != newTitle) {
            changes["title"] = mapOf("before" to existing.title, "after" to newTitle)
        }
        return changes
    }
}
