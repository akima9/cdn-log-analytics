package com.example.cdn.config

import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.DailyStats
import com.example.cdn.domain.DailyStatsRepository
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.MonthlyStats
import com.example.cdn.domain.MonthlyStatsRepository
import com.example.cdn.domain.ProgramRepository
import com.example.cdn.step.ChannelProgramPair
import com.example.cdn.step.ChannelProgramReader
import com.example.cdn.step.DailyStatsProcessor
import com.example.cdn.step.DailyStatsWriter
import com.example.cdn.step.MonthlyStatsProcessor
import com.example.cdn.step.MonthlyStatsWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class StatsAggregationJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val cdnLogRepository: CdnLogRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val monthlyStatsRepository: MonthlyStatsRepository,
) {

    @Bean
    @StepScope
    fun dailyStatsChannelProgramReader(): ChannelProgramReader =
        ChannelProgramReader(channelRepository, programRepository)

    @Bean
    @StepScope
    fun monthlyStatsChannelProgramReader(): ChannelProgramReader =
        ChannelProgramReader(channelRepository, programRepository)

    @Bean
    @StepScope
    fun dailyStatsProcessor(
        @Value("#{jobParameters['targetDate']}") targetDate: String,
    ): DailyStatsProcessor = DailyStatsProcessor(cdnLogRepository, LocalDate.parse(targetDate))

    @Bean
    @StepScope
    fun monthlyStatsProcessor(
        @Value("#{jobParameters['targetDate']}") targetDate: String,
    ): MonthlyStatsProcessor = MonthlyStatsProcessor(cdnLogRepository, LocalDate.parse(targetDate))

    @Bean
    fun dailyStatsWriter(): DailyStatsWriter = DailyStatsWriter(dailyStatsRepository)

    @Bean
    fun monthlyStatsWriter(): MonthlyStatsWriter = MonthlyStatsWriter(monthlyStatsRepository)

    @Bean
    fun dailyStatsStep(
        dailyStatsChannelProgramReader: ChannelProgramReader,
        dailyStatsProcessor: DailyStatsProcessor,
        dailyStatsWriter: DailyStatsWriter,
    ): Step =
        StepBuilder("dailyStatsStep", jobRepository)
            .chunk<ChannelProgramPair, DailyStats>(50, transactionManager)
            .reader(dailyStatsChannelProgramReader)
            .processor(dailyStatsProcessor)
            .writer(dailyStatsWriter)
            .build()

    @Bean
    fun monthlyStatsStep(
        monthlyStatsChannelProgramReader: ChannelProgramReader,
        monthlyStatsProcessor: MonthlyStatsProcessor,
        monthlyStatsWriter: MonthlyStatsWriter,
    ): Step =
        StepBuilder("monthlyStatsStep", jobRepository)
            .chunk<ChannelProgramPair, MonthlyStats>(50, transactionManager)
            .reader(monthlyStatsChannelProgramReader)
            .processor(monthlyStatsProcessor)
            .writer(monthlyStatsWriter)
            .build()

    @Bean
    fun statsAggregationJob(dailyStatsStep: Step, monthlyStatsStep: Step): Job =
        JobBuilder("statsAggregationJob", jobRepository)
            .start(dailyStatsStep)
            .next(monthlyStatsStep)
            .build()
}
