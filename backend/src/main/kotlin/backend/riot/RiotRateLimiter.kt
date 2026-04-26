package backend.riot

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component

@Component
class RiotRateLimiter(
    private val minIntervalMs: Long = 1300L
) {
    private val mutex = Mutex()
    private var lastRequestTime: Long = 0L

    suspend fun <T> withRateLimit(block: suspend () -> T): T {
        mutex.withLock {
            val now = System.currentTimeMillis()
            val elapsed = now - lastRequestTime
            if (elapsed < minIntervalMs) {
                delay(minIntervalMs - elapsed)
            }
            lastRequestTime = System.currentTimeMillis()
        }
        return block()
    }
}
