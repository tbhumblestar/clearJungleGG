package backend.domain.stats

import org.springframework.data.jpa.repository.JpaRepository

interface ChampionStatsRepository : JpaRepository<ChampionStats, ChampionStatsId> {

    fun deleteByPatchVersion(patchVersion: String)
}
