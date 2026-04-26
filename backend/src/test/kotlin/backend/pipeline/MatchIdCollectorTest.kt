package backend.pipeline

import backend.jpa.JungleMatchRecordJpaService
import backend.riot.RiotApiClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode

class MatchIdCollectorTest {

    private val riotApiClient = mockk<RiotApiClient>()
    private val jungleMatchRecordJpaService = mockk<JungleMatchRecordJpaService>()
    private val collector = MatchIdCollector(riotApiClient, jungleMatchRecordJpaService)

    @Test
    fun `collect deduplicates match ids across users`() = runTest {
        coEvery { riotApiClient.getMatchIds("p1", any(), any()) } returns listOf("KR_1", "KR_2")
        coEvery { riotApiClient.getMatchIds("p2", any(), any()) } returns listOf("KR_2", "KR_3")
        every { jungleMatchRecordJpaService.findExistingMatchIds(any()) } returns emptySet()

        val result = collector.collect(listOf("p1", "p2"), 1000, 2000)

        assertThat(result).containsExactlyInAnyOrder("KR_1", "KR_2", "KR_3")
    }

    @Test
    fun `collect excludes already existing match ids`() = runTest {
        coEvery { riotApiClient.getMatchIds("p1", any(), any()) } returns listOf("KR_1", "KR_2", "KR_3")
        every { jungleMatchRecordJpaService.findExistingMatchIds(any()) } returns setOf("KR_1")

        val result = collector.collect(listOf("p1"), 1000, 2000)

        assertThat(result).containsExactlyInAnyOrder("KR_2", "KR_3")
    }

    @Test
    fun `collect skips 404 errors gracefully`() = runTest {
        coEvery { riotApiClient.getMatchIds("p1", any(), any()) } returns listOf("KR_1")
        coEvery { riotApiClient.getMatchIds("p2", any(), any()) } throws
            WebClientResponseException.create(404, "Not Found", HttpHeaders.EMPTY, ByteArray(0), null)
        every { jungleMatchRecordJpaService.findExistingMatchIds(any()) } returns emptySet()

        val result = collector.collect(listOf("p1", "p2"), 1000, 2000)

        assertThat(result).containsExactly("KR_1")
    }
}
