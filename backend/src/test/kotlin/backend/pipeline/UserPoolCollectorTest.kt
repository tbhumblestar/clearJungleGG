package backend.pipeline

import backend.riot.RiotApiClient
import backend.riot.dto.LeagueEntryDto
import backend.riot.dto.LeagueListDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserPoolCollectorTest {

    private val riotApiClient = mockk<RiotApiClient>()
    private val collector = UserPoolCollector(riotApiClient)

    @Test
    fun `collect merges challenger and grandmaster entries`() = runTest {
        coEvery { riotApiClient.getChallengerLeague() } returns LeagueListDto(
            tier = "CHALLENGER",
            entries = listOf(
                LeagueEntryDto(puuid = "c1", leaguePoints = 2000),
                LeagueEntryDto(puuid = "c2", leaguePoints = 1800)
            )
        )
        coEvery { riotApiClient.getGrandmasterLeague() } returns LeagueListDto(
            tier = "GRANDMASTER",
            entries = listOf(
                LeagueEntryDto(puuid = "g1", leaguePoints = 800)
            )
        )

        val result = collector.collect()

        assertThat(result).hasSize(3)
        assertThat(result.filter { it.tier == "CHALLENGER" }).hasSize(2)
        assertThat(result.filter { it.tier == "GRANDMASTER" }).hasSize(1)
        assertThat(result.map { it.puuid }).containsExactly("c1", "c2", "g1")
    }
}
