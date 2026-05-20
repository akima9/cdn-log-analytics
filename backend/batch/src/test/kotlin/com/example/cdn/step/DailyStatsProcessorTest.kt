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

class DailyStatsProcessorTest {

    private val cdnLogRepository = mockk<CdnLogRepository>()
    private val targetDate = LocalDate.of(2026, 5, 18)

    private val channel = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val program = Program(id = 1L, channel = channel, name = "아침 뉴스", code = "MORNING_NEWS")
    private val pair = ChannelProgramPair(channel, program)

    private val processor = DailyStatsProcessor(cdnLogRepository, targetDate)

    private fun log(hour: Int, status: Short, bytes: Long, ip: String) = CdnLog(
        cdnProvider = "CloudFront",
        requestTime = LocalDateTime.of(2026, 5, 18, hour, 0),
        channel = channel,
        program = program,
        ip = ip,
        status = status,
        bytes = bytes,
        edgeLocation = "ICN50",
    )

    @Test
    fun `CdnLog 목록으로 DailyStats를 올바르게 집계한다`() {
        every {
            cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(
                channel, program,
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay().minusNanos(1),
            )
        } returns listOf(
            log(9, 200, 1000L, "1.1.1.1"),
            log(9, 200, 3000L, "2.2.2.2"),
            log(10, 404, 2000L, "1.1.1.1"),
            log(10, 500, 4000L, "3.3.3.3"),
        )

        val result = processor.process(pair)!!

        assertThat(result.statDate).isEqualTo(targetDate)
        assertThat(result.channel).isEqualTo(channel)
        assertThat(result.program).isEqualTo(program)
        assertThat(result.totalRequests).isEqualTo(4L)
        assertThat(result.totalBytes).isEqualTo(10000L)
        assertThat(result.errorCount).isEqualTo(2L)
        assertThat(result.avgBytes).isEqualByComparingTo("2500.00")
        assertThat(result.uniqueIps).isEqualTo(3)
        assertThat(result.peakHour).isEqualTo(9.toByte())
    }

    @Test
    fun `CdnLog가 없으면 null을 반환한다`() {
        every {
            cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(any(), any(), any(), any())
        } returns emptyList()

        assertThat(processor.process(pair)).isNull()
    }

    @Test
    fun `peakHour 동률 시 낮은 시간대를 선택한다`() {
        every {
            cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(any(), any(), any(), any())
        } returns listOf(
            log(14, 200, 100L, "1.1.1.1"),
            log(14, 200, 100L, "2.2.2.2"),
            log(9, 200, 100L, "3.3.3.3"),
            log(9, 200, 100L, "4.4.4.4"),
        )

        val result = processor.process(pair)!!

        assertThat(result.peakHour).isEqualTo(9.toByte())
    }
}
