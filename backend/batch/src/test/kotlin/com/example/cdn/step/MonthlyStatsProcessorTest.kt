package com.example.cdn.step

import com.example.cdn.domain.CdnLog
import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.Channel
import com.example.cdn.domain.Program
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class MonthlyStatsProcessorTest {

    private val cdnLogRepository = mockk<CdnLogRepository>()
    private val targetDate = LocalDate.of(2026, 5, 18)

    private val channel = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val program = Program(id = 1L, channel = channel, name = "아침 뉴스", code = "MORNING_NEWS")
    private val pair = ChannelProgramPair(channel, program)

    private val processor = MonthlyStatsProcessor(cdnLogRepository, targetDate)

    private fun log(date: LocalDate, status: Short, bytes: Long, ip: String) = CdnLog(
        cdnProvider = "CloudFront",
        requestTime = LocalDateTime.of(date.year, date.month, date.dayOfMonth, 9, 0),
        channel = channel,
        program = program,
        ip = ip,
        status = status,
        bytes = bytes,
        edgeLocation = "ICN50",
    )

    @Test
    fun `targetDate가 속한 월 전체 CdnLog로 MonthlyStats를 집계한다`() {
        val monthStart = LocalDate.of(2026, 5, 1).atStartOfDay()
        val monthEnd = LocalDate.of(2026, 6, 1).atStartOfDay().minusNanos(1)
        every {
            cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(channel, program, monthStart, monthEnd)
        } returns listOf(
            log(LocalDate.of(2026, 5, 1), 200, 1000L, "1.1.1.1"),
            log(LocalDate.of(2026, 5, 15), 404, 2000L, "2.2.2.2"),
            log(LocalDate.of(2026, 5, 31), 200, 3000L, "1.1.1.1"),
        )

        val result = processor.process(pair)!!

        assertThat(result.statYear).isEqualTo(2026)
        assertThat(result.statMonth).isEqualTo(5)
        assertThat(result.totalRequests).isEqualTo(3L)
        assertThat(result.totalBytes).isEqualTo(6000L)
        assertThat(result.errorCount).isEqualTo(1L)
        assertThat(result.avgBytes).isEqualByComparingTo("2000.00")
        assertThat(result.uniqueIps).isEqualTo(2)
    }

    @Test
    fun `월 전체 CdnLog가 없으면 null을 반환한다`() {
        every {
            cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(any(), any(), any(), any())
        } returns emptyList()

        assertThat(processor.process(pair)).isNull()
    }
}
