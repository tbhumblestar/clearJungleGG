package backend.domain.match

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "pure_fullcamp_clear_record")
class PureFullcampClearRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "jungle_match_record_id", nullable = false, unique = true)
    val matchRecordId: Long,

    @Column(name = "clear_time_ms", nullable = false)
    val clearTimeMs: Int,

    @Column(name = "youtube_video_id", length = 20)
    val youtubeVideoId: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
