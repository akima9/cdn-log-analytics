package com.example.cdn.config

import com.example.cdn.domain.CdnLog
import com.example.cdn.step.CloudFrontLogFieldSetMapper
import com.example.cdn.step.CloudFrontLogProcessor
import com.example.cdn.step.CloudFrontLogRecord
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class LogParsingJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val processor: CloudFrontLogProcessor,
    private val properties: MockProperties,
) {

    @Bean
    @StepScope
    fun cloudFrontLogReader(
        @Value("#{jobParameters['targetDate']}") targetDate: String?,
    ): FlatFileItemReader<CloudFrontLogRecord> {
        val date = targetDate?.let { LocalDate.parse(it) } ?: LocalDate.now().minusDays(1)
        val fileName = "cloudfront_${date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}.log"
        return FlatFileItemReaderBuilder<CloudFrontLogRecord>()
            .name("cloudFrontLogReader")
            .resource(FileSystemResource("${properties.outputDir}/$fileName"))
            .delimited().delimiter("\t")
            .names(
                "date", "time", "edgeLocation", "bytes", "clientIp", "method",
                "host", "uriStem", "status", "userAgent", "resultType", "protocol",
            )
            .fieldSetMapper(CloudFrontLogFieldSetMapper())
            .build()
    }

    @Bean
    fun logParsingStep(): Step =
        StepBuilder("logParsingStep", jobRepository)
            .chunk<CloudFrontLogRecord, CdnLog>(1000, transactionManager)
            .reader(cloudFrontLogReader(null))
            .processor(processor)
            .writer(
                JpaItemWriterBuilder<CdnLog>()
                    .entityManagerFactory(entityManagerFactory)
                    .usePersist(true)
                    .build(),
            )
            .build()

    @Bean
    fun logParsingJob(logParsingStep: Step): Job =
        JobBuilder("logParsingJob", jobRepository)
            .start(logParsingStep)
            .build()
}
