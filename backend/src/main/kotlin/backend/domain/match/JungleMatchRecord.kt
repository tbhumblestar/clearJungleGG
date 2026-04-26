package backend.domain.match

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "jungle_match_record")
class JungleMatchRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "match_id", length = 30, nullable = false)
    val matchId: String,

    @Column(name = "champion_id", length = 50, nullable = false)
    val championId: String,

    @Column(name = "team", length = 4, nullable = false)
    val team: String,

    @Column(name = "win", nullable = false)
    val win: Boolean,

    @Column(name = "opponent_champion_id", length = 50, nullable = false)
    val opponentChampionId: String,

    @Column(name = "runes", columnDefinition = "jsonb", nullable = false)
    val runes: String,

    @Column(name = "summoner_spells", length = 10, nullable = false)
    val summonerSpells: String,

    @Column(name = "start_position", length = 20)
    var startPosition: String? = null,

    @Column(name = "banned_champions", columnDefinition = "jsonb", nullable = false)
    val bannedChampions: String = "[]",

    @Column(name = "patch_version", length = 20, nullable = false)
    val patchVersion: String,

    @Column(name = "summoner_name", length = 50, nullable = false)
    val summonerName: String,

    @Column(name = "summoner_tag", length = 10, nullable = false)
    val summonerTag: String,

    @Column(name = "summoner_tier", length = 20)
    val summonerTier: String? = null,

    @Column(name = "game_started_at", nullable = false)
    val gameStartedAt: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
