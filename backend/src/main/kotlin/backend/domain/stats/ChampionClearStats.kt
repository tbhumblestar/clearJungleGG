package backend.domain.stats

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

data class ChampionClearStatsId(
    val championId: String = "",
    val patchVersion: String = "",
    val team: String = "",
    val startPosition: String = ""
) : Serializable

@Entity
@Table(name = "champion_clear_stats")
@IdClass(ChampionClearStatsId::class)
class ChampionClearStats(
    @Id
    @Column(name = "champion_id", length = 50)
    val championId: String,

    @Id
    @Column(name = "patch_version", length = 20)
    val patchVersion: String,

    @Id
    @Column(name = "team", length = 4)
    val team: String,

    @Id
    @Column(name = "start_position", length = 20)
    val startPosition: String,

    @Column(name = "avg_clear_time_ms", nullable = false)
    var avgClearTimeMs: Int = 0,

    @Column(name = "best_clear_time_ms", nullable = false)
    var bestClearTimeMs: Int = 0,

    @Column(name = "sample_count", nullable = false)
    var sampleCount: Int = 0,

    @Column(name = "start_count", nullable = false)
    var startCount: Int = 0,

    @Column(name = "total_games", nullable = false)
    var totalGames: Int = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
