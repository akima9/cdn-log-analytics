package com.example.cdn.step

import com.example.cdn.domain.CdnLogRepository
import com.example.cdn.domain.MonthlyStats
import org.springframework.batch.item.ItemProcessor
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

open class MonthlyStatsProcessor(
    private val cdnLogRepository: CdnLogRepository,
    private val targetDate: LocalDate,
) : ItemProcessor<ChannelProgramPair, MonthlyStats> {

    override fun process(item: ChannelProgramPair): MonthlyStats? {
        val monthStart = targetDate.withDayOfMonth(1).atStartOfDay()
        val monthEnd = targetDate.withDayOfMonth(1).plusMonths(1).atStartOfDay().minusNanos(1)
        val logs = cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(
            item.channel, item.program, monthStart, monthEnd
        )
        if (logs.isEmpty()) return null

        val totalRequests = logs.size.toLong()
        val totalBytes = logs.sumOf { it.bytes }
        val errorCount = logs.count { it.status >= 400 }.toLong()
        val avgBytes = totalBytes.toBigDecimal().divide(totalRequests.toBigDecimal(), 2, RoundingMode.HALF_UP)
        val uniqueIps = logs.distinctBy { it.ip }.size

        return MonthlyStats(
            statYear = targetDate.year,
            statMonth = targetDate.monthValue,
            channel = item.channel,
            program = item.program,
            totalRequests = totalRequests,
            totalBytes = totalBytes,
            errorCount = errorCount,
            avgBytes = avgBytes,
            uniqueIps = uniqueIps,
        )
    }
}
