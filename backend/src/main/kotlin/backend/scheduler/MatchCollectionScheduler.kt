package backend.scheduler

import backend.pipeline.MatchCollectionPipeline
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

@Component
@EnableScheduling
class MatchCollectionScheduler(private val pipeline: MatchCollectionPipeline) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    fun runDaily() {
        val yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1)
        log.info("Scheduled pipeline execution for $yesterday")
        runBlocking {
            try {
                pipeline.execute(yesterday)
            } catch (e: Exception) {
                log.error("Scheduled pipeline failed for $yesterday", e)
            }
        }
    }
}
