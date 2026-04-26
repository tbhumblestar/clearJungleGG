package backend.riot

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RiotRateLimiterTest {

    @Test
    fun `withRateLimit executes block and returns result`() = runTest {
        val limiter = RiotRateLimiter(minIntervalMs = 0)

        val result = limiter.withRateLimit { "hello" }

        assertThat(result).isEqualTo("hello")
    }

    @Test
    fun `consecutive calls are spaced by minimum interval`() = runBlocking {
        val limiter = RiotRateLimiter(minIntervalMs = 100)
        val timestamps = mutableListOf<Long>()

        repeat(3) {
            limiter.withRateLimit {
                timestamps.add(System.currentTimeMillis())
            }
        }

        for (i in 1 until timestamps.size) {
            assertThat(timestamps[i] - timestamps[i - 1]).isGreaterThanOrEqualTo(80)
        }
    }
}
