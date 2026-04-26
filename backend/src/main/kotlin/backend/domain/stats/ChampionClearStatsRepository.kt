package backend.domain.stats

import org.springframework.data.jpa.repository.JpaRepository

interface ChampionClearStatsRepository : JpaRepository<ChampionClearStats, ChampionClearStatsId> {

    fun deleteByPatchVersion(patchVersion: String)
}
