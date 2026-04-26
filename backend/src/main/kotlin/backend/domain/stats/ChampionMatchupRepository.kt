package backend.domain.stats

import org.springframework.data.jpa.repository.JpaRepository

interface ChampionMatchupRepository : JpaRepository<ChampionMatchup, ChampionMatchupId> {

    fun deleteByPatchVersion(patchVersion: String)
}
