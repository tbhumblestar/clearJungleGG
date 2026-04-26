package backend.controller

import backend.pipeline.MatchCollectionPipeline
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/pipeline")
class PipelineController(private val pipeline: MatchCollectionPipeline) {

    @PostMapping("/run")
    suspend fun run(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam(required = false) limit: Int? = null
    ): MatchCollectionPipeline.PipelineResult {
        return pipeline.execute(date, limit)
    }
}
