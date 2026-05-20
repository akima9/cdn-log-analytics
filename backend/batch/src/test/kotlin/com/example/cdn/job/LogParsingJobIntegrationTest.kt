package com.example.cdn.job

import com.example.cdn.config.MockProperties
import com.example.cdn.domain.Channel
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.Program
import com.example.cdn.domain.ProgramRepository
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
import java.io.File
import java.time.LocalDate

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class LogParsingJobIntegrationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    @Qualifier("logParsingJob")
    private lateinit var logParsingJob: Job

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var programRepository: ProgramRepository

    @Autowired
    private lateinit var cdnLogRepository: CdnLogRepository

    @Autowired
    private lateinit var mockProperties: MockProperties

    private lateinit var channel: Channel
    private val targetDate = LocalDate.of(2025, 5, 18)

    @BeforeEach
    fun setUp() {
        jobLauncherTestUtils.job = logParsingJob
        cdnLogRepository.deleteAll()
        channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        createTestLogFile()
    }

    @AfterEach
    fun tearDown() {
        val file = File("${mockProperties.outputDir}/cloudfront_20250518.log")
        if (file.exists()) file.delete()
        cdnLogRepository.deleteAll()
        programRepository.deleteAll()
        channelRepository.deleteAll()
    }

    @Test
    fun `로그 파일을 읽어 cdn_logs 테이블에 적재한다`() {
        val params = JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()

        val execution = jobLauncherTestUtils.launchJob(params)

        assertEquals(BatchStatus.COMPLETED, execution.status)
        assertEquals(2L, cdnLogRepository.count())
    }

    private fun createTestLogFile() {
        val dir = File(mockProperties.outputDir)
        dir.mkdirs()
        val file = File(dir, "cloudfront_20250518.log")
        val t = "\t"
        val lines = listOf(
            "#Version: 1.0",
            "#Fields: date time x-edge-location sc-bytes c-ip cs-method cs(Host) cs-uri-stem sc-status cs(User-Agent) x-edge-result-type cs-protocol",
            "2025-05-18${t}01:00:00${t}ICN50${t}10000${t}1.1.1.1${t}GET${t}d123.cloudfront.net${t}/live/NEWS/MORNING_NEWS/index.m3u8${t}200${t}Mozilla/5.0${t}Hit${t}https",
            "2025-05-18${t}02:00:00${t}ICN50${t}20000${t}2.2.2.2${t}GET${t}d123.cloudfront.net${t}/live/NEWS/MORNING_NEWS/index.m3u8${t}206${t}Mozilla/5.0${t}Hit${t}https",
            "2025-05-18${t}03:00:00${t}ICN50${t}5000${t}3.3.3.3${t}GET${t}d123.cloudfront.net${t}/live/UNKNOWN/INVALID/index.m3u8${t}200${t}Mozilla/5.0${t}Miss${t}https",
        )
        file.writeText(lines.joinToString("\n"))
    }
}
