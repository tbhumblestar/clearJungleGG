package backend.domain.stats

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

data class ChampionStatsId(
    val championId: String = "",
    val patchVersion: String = ""
) : Serializable

@Entity
@Table(name = "champion_stats")
@IdClass(ChampionStatsId::class)
class ChampionStats(
    @Id
    @Column(name = "champion_id", length = 50)
    val championId: String,

    @Id
    @Column(name = "patch_version", length = 20)
    val patchVersion: String,

    @Column(name = "pick_count", nullable = false)
    var pickCount: Int = 0,

    @Column(name = "win_count", nullable = false)
    var winCount: Int = 0,

    @Column(name = "ban_count", nullable = false)
    var banCount: Int = 0,

    @Column(name = "total_matches", nullable = false)
    var totalMatches: Int = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
