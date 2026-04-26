package backend.domain.match

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JungleMatchRecordRepository : JpaRepository<JungleMatchRecord, Long> {

    @Query("SELECT mr.matchId FROM JungleMatchRecord mr WHERE mr.matchId IN :matchIds")
    fun findExistingMatchIds(@Param("matchIds") matchIds: Collection<String>): List<String>

    fun findByMatchIdIn(matchIds: Collection<String>): List<JungleMatchRecord>

    @Query("SELECT DISTINCT mr.patchVersion FROM JungleMatchRecord mr")
    fun findDistinctPatchVersions(): List<String>

    fun findByPatchVersion(patchVersion: String): List<JungleMatchRecord>
}
