package backend.jpa

import backend.domain.match.PureFullcampClearRecord
import backend.domain.match.PureFullcampClearRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PureFullcampClearRecordJpaService(private val pureFullcampClearRecordRepository: PureFullcampClearRecordRepository) {

    @Transactional
    fun saveAll(records: List<PureFullcampClearRecord>): List<PureFullcampClearRecord> =
        pureFullcampClearRecordRepository.saveAll(records)

    @Transactional(readOnly = true)
    fun findAll(): List<PureFullcampClearRecord> =
        pureFullcampClearRecordRepository.findAll()
}
