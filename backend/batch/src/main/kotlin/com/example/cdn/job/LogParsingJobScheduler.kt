package com.example.cdn.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class LogParsingJobScheduler(
    private val jobLauncher: JobLauncher,
    @Qualifier("logParsingJob") private val job: Job,
) {
    @Scheduled(cron = "0 30 1 * * *")
    fun runYesterday() {
        val params = JobParametersBuilder()
            .addString("targetDate", LocalDate.now().minusDays(1).toString())
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(job, params)
    }
}
