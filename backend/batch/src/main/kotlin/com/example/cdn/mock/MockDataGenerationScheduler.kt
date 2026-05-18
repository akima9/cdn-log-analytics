package com.example.cdn.mock

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MockDataGenerationScheduler(private val writer: MockLogFileWriter) {

    @Scheduled(cron = "0 0 1 * * *")
    fun generateYesterday() = writer.write(LocalDate.now().minusDays(1))
}
