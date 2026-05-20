package com.example.cdn.step

import com.example.cdn.domain.MonthlyStats
import com.example.cdn.domain.MonthlyStatsRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class MonthlyStatsWriter(
    private val monthlyStatsRepository: MonthlyStatsRepository,
) : ItemWriter<MonthlyStats> {

    override fun write(chunk: Chunk<out MonthlyStats>) {
        chunk.items.forEach { stats ->
            monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(
                stats.statYear, stats.statMonth, stats.channel, stats.program
            )
            monthlyStatsRepository.flush()
            monthlyStatsRepository.save(stats)
        }
    }
}
