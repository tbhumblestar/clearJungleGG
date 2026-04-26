package backend.jpa

import backend.domain.match.JungleMatchRecord
import backend.domain.match.JungleMatchRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JungleMatchRecordJpaService(private val jungleMatchRecordRepository: JungleMatchRecordRepository) {

    @Transactional
    fun saveAll(records: List<JungleMatchRecord>): List<JungleMatchRecord> =
        jungleMatchRecordRepository.saveAll(records)

    @Transactional(readOnly = true)
    fun findExistingMatchIds(matchIds: Collection<String>): Set<String> =
        jungleMatchRecordRepository.findExistingMatchIds(matchIds).toSet()

    @Transactional(readOnly = true)
    fun findByMatchIds(matchIds: Collection<String>): List<JungleMatchRecord> =
        jungleMatchRecordRepository.findByMatchIdIn(matchIds)

    @Transactional(readOnly = true)
    fun findAllByIds(ids: Collection<Long>): List<JungleMatchRecord> =
        jungleMatchRecordRepository.findAllById(ids)

    @Transactional(readOnly = true)
    fun findDistinctPatchVersions(): List<String> =
        jungleMatchRecordRepository.findDistinctPatchVersions()

    @Transactional(readOnly = true)
    fun findByPatchVersion(patchVersion: String): List<JungleMatchRecord> =
        jungleMatchRecordRepository.findByPatchVersion(patchVersion)
}
