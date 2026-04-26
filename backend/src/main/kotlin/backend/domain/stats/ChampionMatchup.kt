package backend.domain.stats

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

data class ChampionMatchupId(
    val championId: String = "",
    val opponentChampionId: String = "",
    val patchVersion: String = ""
) : Serializable

@Entity
@Table(name = "champion_matchup")
@IdClass(ChampionMatchupId::class)
class ChampionMatchup(
    @Id
    @Column(name = "champion_id", length = 50)
    val championId: String,

    @Id
    @Column(name = "opponent_champion_id", length = 50)
    val opponentChampionId: String,

    @Id
    @Column(name = "patch_version", length = 20)
    val patchVersion: String,

    @Column(name = "wins", nullable = false)
    var wins: Int = 0,

    @Column(name = "losses", nullable = false)
    var losses: Int = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
