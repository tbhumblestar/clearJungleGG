package backend.domain.champion

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "champion")
class Champion(
    @Id
    @Column(name = "id", length = 50)
    val id: String,

    @Column(name = "key", nullable = false)
    val key: Int,

    @Column(name = "name", length = 50, nullable = false)
    var name: String,

    @Column(name = "title", length = 100, nullable = false)
    var title: String,

    @Column(name = "patch_version", length = 20, nullable = false)
    var patchVersion: String,

    @Column(name = "champion_history", columnDefinition = "jsonb", nullable = false)
    var championHistory: String = "[]",

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
