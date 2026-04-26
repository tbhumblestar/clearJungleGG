package backend.jpa

import backend.domain.champion.Champion
import backend.domain.champion.ChampionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChampionJpaService(private val championRepository: ChampionRepository) {

    @Transactional
    fun saveAll(champions: List<Champion>): List<Champion> =
        championRepository.saveAll(champions)

    @Transactional(readOnly = true)
    fun findAll(): List<Champion> =
        championRepository.findAll()
}
