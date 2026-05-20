package com.example.cdn.job

import com.example.cdn.domain.CdnLog
import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.Channel
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.DailyStatsRepository
import com.example.cdn.domain.MonthlyStatsRepository
import com.example.cdn.domain.Program
import com.example.cdn.domain.ProgramRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class StatsAggregationJobIntegrationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    @Qualifier("statsAggregationJob")
    private lateinit var statsAggregationJob: Job

    @Autowired private lateinit var channelRepository: ChannelRepository
    @Autowired private lateinit var programRepository: ProgramRepository
    @Autowired private lateinit var cdnLogRepository: CdnLogRepository
    @Autowired private lateinit var dailyStatsRepository: DailyStatsRepository
    @Autowired private lateinit var monthlyStatsRepository: MonthlyStatsRepository

    private lateinit var channel: Channel
    private lateinit var morningNews: Program
    private lateinit var eveningNews: Program
    private val targetDate = LocalDate.of(2026, 5, 18)

    @BeforeEach
    fun setUp() {
        jobLauncherTestUtils.job = statsAggregationJob
        monthlyStatsRepository.deleteAll()
        dailyStatsRepository.deleteAll()
        cdnLogRepository.deleteAll()
        programRepository.deleteAll()
        channelRepository.deleteAll()

        channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        morningNews = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        eveningNews = programRepository.save(Program(channel = channel, name = "저녁 뉴스", code = "EVENING_NEWS"))

        fun log(program: Program, date: LocalDate, hour: Int, status: Short, bytes: Long, ip: String) = CdnLog(
            cdnProvider = "CloudFront",
            requestTime = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hour, 0),
            channel = channel, program = program,
            ip = ip, status = status, bytes = bytes, edgeLocation = "ICN50",
        )
        cdnLogRepository.saveAll(listOf(
            log(morningNews, targetDate, 9, 200, 1000L, "1.1.1.1"),
            log(morningNews, targetDate, 9, 404, 2000L, "2.2.2.2"),
            log(morningNews, targetDate, 22, 200, 3000L, "1.1.1.1"),
            log(eveningNews, targetDate, 20, 200, 5000L, "3.3.3.3"),
            log(morningNews, targetDate.minusDays(1), 9, 200, 9999L, "9.9.9.9"),
        ))
    }

    @AfterEach
    fun tearDown() {
        monthlyStatsRepository.deleteAll()
        dailyStatsRepository.deleteAll()
        cdnLogRepository.deleteAll()
        programRepository.deleteAll()
        channelRepository.deleteAll()
    }

    @Test
    fun `statsAggregationJob이 daily_stats와 monthly_stats를 정확히 생성한다`() {
        val params = JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()

        val execution = jobLauncherTestUtils.launchJob(params)

        assertEquals(BatchStatus.COMPLETED, execution.status)

        val dailyList = dailyStatsRepository.findAll()
        assertThat(dailyList).hasSize(2)

        val morning = dailyList.first { it.program.code == "MORNING_NEWS" }
        assertThat(morning.totalRequests).isEqualTo(3L)
        assertThat(morning.totalBytes).isEqualTo(6000L)
        assertThat(morning.errorCount).isEqualTo(1L)
        assertThat(morning.avgBytes).isEqualByComparingTo("2000.00")
        assertThat(morning.uniqueIps).isEqualTo(2)
        assertThat(morning.peakHour).isEqualTo(9.toByte())

        val evening = dailyList.first { it.program.code == "EVENING_NEWS" }
        assertThat(evening.totalRequests).isEqualTo(1L)
        assertThat(evening.peakHour).isEqualTo(20.toByte())

        val monthlyList = monthlyStatsRepository.findAll()
        assertThat(monthlyList).hasSize(2)

        val morningMonthly = monthlyList.first { it.program.code == "MORNING_NEWS" }
        assertThat(morningMonthly.statYear).isEqualTo(2026)
        assertThat(morningMonthly.statMonth).isEqualTo(5)
        assertThat(morningMonthly.totalRequests).isEqualTo(4L)
        assertThat(morningMonthly.totalBytes).isEqualTo(15999L)
    }

    @Test
    fun `같은 targetDate로 두 번 실행해도 중복 없이 최신 결과로 갱신된다`() {
        val params1 = JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()
        jobLauncherTestUtils.launchJob(params1)

        val params2 = JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()
        val execution = jobLauncherTestUtils.launchJob(params2)

        assertEquals(BatchStatus.COMPLETED, execution.status)
        assertThat(dailyStatsRepository.count()).isEqualTo(2L)
        assertThat(monthlyStatsRepository.count()).isEqualTo(2L)
    }
}
