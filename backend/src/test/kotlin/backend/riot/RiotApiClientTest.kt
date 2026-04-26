package backend.riot

import backend.riot.dto.LeagueListDto
import backend.riot.dto.MatchDetailDto
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class RiotApiClientTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var client: RiotApiClient

    @BeforeEach
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        val baseUrl = mockServer.url("/").toString().trimEnd('/')
        val webClient = WebClient.builder().baseUrl(baseUrl).build()
        val rateLimiter = RiotRateLimiter(minIntervalMs = 0)

        client = RiotApiClient(
            krWebClient = webClient,
            asiaWebClient = webClient,
            ddragonWebClient = webClient,
            rateLimiter = rateLimiter
        )
    }

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun `getChallengerLeague returns parsed entries`() = runTest {
        mockServer.enqueue(jsonResponse(readFixture("challenger_league.json")))

        val result = client.getChallengerLeague()

        assertThat(result.tier).isEqualTo("CHALLENGER")
        assertThat(result.entries).hasSize(3)
        assertThat(result.entries[0].puuid).isEqualTo("puuid-challenger-1")
    }

    @Test
    fun `getGrandmasterLeague returns parsed entries`() = runTest {
        mockServer.enqueue(jsonResponse(readFixture("grandmaster_league.json")))

        val result = client.getGrandmasterLeague()

        assertThat(result.tier).isEqualTo("GRANDMASTER")
        assertThat(result.entries).hasSize(2)
    }

    @Test
    fun `getMatchIds returns list of match id strings`() = runTest {
        mockServer.enqueue(jsonResponse(readFixture("match_ids.json")))

        val result = client.getMatchIds("puuid-1", 1714000000, 1714086399)

        assertThat(result).containsExactly("KR_7001", "KR_7002", "KR_7003")
    }

    @Test
    fun `getMatchDetail parses junglers and bans`() = runTest {
        mockServer.enqueue(jsonResponse(readFixture("match_detail.json")))

        val result = client.getMatchDetail("KR_7001")

        assertThat(result.metadata.matchId).isEqualTo("KR_7001")
        assertThat(result.info.gameDuration).isEqualTo(1800)
        assertThat(result.info.gameVersion).isEqualTo("16.6.756.9613")

        val junglers = result.info.participants.filter { it.teamPosition == "JUNGLE" }
        assertThat(junglers).hasSize(2)
        assertThat(junglers[0].championName).isEqualTo("LeeSin")
        assertThat(junglers[1].championName).isEqualTo("Graves")

        assertThat(result.info.teams).hasSize(2)
        assertThat(result.info.teams.flatMap { it.bans }).hasSize(10)
    }

    @Test
    fun `getMatchDetail handles 429 with retry`() = runTest {
        mockServer.enqueue(MockResponse().setResponseCode(429).setHeader("Retry-After", "1"))
        mockServer.enqueue(jsonResponse(readFixture("match_detail.json")))

        val result = client.getMatchDetail("KR_7001")

        assertThat(result.metadata.matchId).isEqualTo("KR_7001")
        assertThat(mockServer.requestCount).isEqualTo(2)
    }

    @Test
    fun `getLatestVersion returns first version`() = runTest {
        mockServer.enqueue(jsonResponse("""["16.6.1", "16.5.1", "16.4.1"]"""))

        val result = client.getLatestVersion()

        assertThat(result).isEqualTo("16.6.1")
    }

    @Test
    fun `getChampionData parses champion entries`() = runTest {
        mockServer.enqueue(jsonResponse(readFixture("champion_ddragon.json")))

        val result = client.getChampionData("16.6.1")

        assertThat(result.data).hasSize(5)
        assertThat(result.data["LeeSin"]?.name).isEqualTo("리 신")
        assertThat(result.data["LeeSin"]?.key).isEqualTo("64")
    }

    private fun readFixture(name: String): String =
        javaClass.classLoader.getResource("fixtures/$name")!!.readText()

    private fun jsonResponse(body: String): MockResponse =
        MockResponse()
            .setBody(body)
            .setHeader("Content-Type", "application/json")
}
