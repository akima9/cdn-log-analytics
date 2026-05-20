package com.example.cdn.step

import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.DailyStats
import org.springframework.batch.item.ItemProcessor
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

open class DailyStatsProcessor(
    private val cdnLogRepository: CdnLogRepository,
    private val targetDate: LocalDate,
) : ItemProcessor<ChannelProgramPair, DailyStats> {

    override fun process(item: ChannelProgramPair): DailyStats? {
        val start = targetDate.atStartOfDay()
        val end = targetDate.plusDays(1).atStartOfDay().minusNanos(1)
        val logs = cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(
            item.channel, item.program, start, end
        )
        if (logs.isEmpty()) return null

        val totalRequests: Long = logs.size.toLong()
        val totalBytes: Long = logs.sumOf { it.bytes }
        val errorCount: Long = logs.count { it.status >= 400 }.toLong()
        val avgBytes: BigDecimal = totalBytes.toBigDecimal().divide(totalRequests.toBigDecimal(), 2, RoundingMode.HALF_UP)
        val uniqueIps: Int = logs.distinctBy { it.ip }.size
        val hourCounts = logs.groupingBy { it.requestTime.hour }.eachCount()
        val maxCount = hourCounts.values.max()
        val peakHour: Byte? = hourCounts.filter { it.value == maxCount }.keys.min().toByte()

        return DailyStats(
            statDate = targetDate,
            channel = item.channel,
            program = item.program,
            totalRequests = totalRequests,
            totalBytes = totalBytes,
            errorCount = errorCount,
            avgBytes = avgBytes,
            uniqueIps = uniqueIps,
            peakHour = peakHour,
        )
    }
}