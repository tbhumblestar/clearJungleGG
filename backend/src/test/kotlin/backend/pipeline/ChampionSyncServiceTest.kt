package backend.pipeline

import backend.domain.champion.Champion
import backend.jpa.ChampionJpaService
import backend.riot.RiotApiClient
import backend.riot.dto.ChampionDataDragonDto
import backend.riot.dto.ChampionDragonEntryDto
import tools.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChampionSyncServiceTest {

    private val riotApiClient = mockk<RiotApiClient>()
    private val championJpaService = mockk<ChampionJpaService>()
    private val objectMapper = jacksonObjectMapper()
    private val service = ChampionSyncService(riotApiClient, championJpaService, objectMapper)

    @Test
    fun `sync creates new champions when DB is empty`() = runTest {
        coEvery { riotApiClient.getLatestVersion() } returns "16.6.1"
        coEvery { riotApiClient.getChampionData("16.6.1") } returns ChampionDataDragonDto(
            version = "16.6.1",
            data = mapOf(
                "LeeSin" to ChampionDragonEntryDto("LeeSin", "64", "리 신", "눈먼 수도승"),
                "Graves" to ChampionDragonEntryDto("Graves", "104", "그레이브즈", "무법자")
            )
        )
        every { championJpaService.findAll() } returns emptyList()
        val saved = slot<List<Champion>>()
        every { championJpaService.saveAll(capture(saved)) } answers { saved.captured }

        val result = service.sync()

        assertThat(result.created).isEqualTo(2)
        assertThat(result.updated).isEqualTo(0)
        assertThat(result.total).isEqualTo(2)
        assertThat(saved.captured.map { it.id }).containsExactlyInAnyOrder("LeeSin", "Graves")
    }

    @Test
    fun `sync detects name change and updates history`() = runTest {
        coEvery { riotApiClient.getLatestVersion() } returns "16.7.1"
        coEvery { riotApiClient.getChampionData("16.7.1") } returns ChampionDataDragonDto(
            version = "16.7.1",
            data = mapOf(
                "LeeSin" to ChampionDragonEntryDto("LeeSin", "64", "리 신(변경)", "눈먼 수도승")
            )
        )
        val existing = Champion("LeeSin", 64, "리 신", "눈먼 수도승", "16.6")
        every { championJpaService.findAll() } returns listOf(existing)
        val saved = slot<List<Champion>>()
        every { championJpaService.saveAll(capture(saved)) } answers { saved.captured }

        val result = service.sync()

        assertThat(result.updated).isEqualTo(1)
        assertThat(result.created).isEqualTo(0)

        val updatedChampion = saved.captured.first()
        assertThat(updatedChampion.name).isEqualTo("리 신(변경)")
        assertThat(updatedChampion.patchVersion).isEqualTo("16.7")

        val history = objectMapper.readTree(updatedChampion.championHistory)
        assertThat(history).hasSize(1)
        assertThat(history[0]["changes"]["name"]["before"].asText()).isEqualTo("리 신")
        assertThat(history[0]["changes"]["name"]["after"].asText()).isEqualTo("리 신(변경)")
    }
}
