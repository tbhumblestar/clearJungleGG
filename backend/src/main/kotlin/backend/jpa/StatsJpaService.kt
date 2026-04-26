package backend.jpa

import backend.domain.stats.ChampionClearStats
import backend.domain.stats.ChampionClearStatsRepository
import backend.domain.stats.ChampionMatchup
import backend.domain.stats.ChampionMatchupRepository
import backend.domain.stats.ChampionStats
import backend.domain.stats.ChampionStatsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StatsJpaService(
    private val championStatsRepository: ChampionStatsRepository,
    private val championMatchupRepository: ChampionMatchupRepository,
    private val championClearStatsRepository: ChampionClearStatsRepository
) {
    @Transactional
    fun replaceChampionStats(patchVersion: String, stats: List<ChampionStats>) {
        championStatsRepository.deleteByPatchVersion(patchVersion)
        championStatsRepository.saveAll(stats)
    }

    @Transactional
    fun replaceChampionMatchups(patchVersion: String, matchups: List<ChampionMatchup>) {
        championMatchupRepository.deleteByPatchVersion(patchVersion)
        championMatchupRepository.saveAll(matchups)
    }

    @Transactional
    fun replaceChampionClearStats(patchVersion: String, stats: List<ChampionClearStats>) {
        championClearStatsRepository.deleteByPatchVersion(patchVersion)
        championClearStatsRepository.saveAll(stats)
    }
}
