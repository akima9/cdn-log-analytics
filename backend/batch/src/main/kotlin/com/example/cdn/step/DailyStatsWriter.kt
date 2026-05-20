package com.example.cdn.step

import com.example.cdn.domain.DailyStats
import com.example.cdn.domain.DailyStatsRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class DailyStatsWriter(
    private val dailyStatsRepository: DailyStatsRepository,
) : ItemWriter<DailyStats> {

    override fun write(chunk: Chunk<out DailyStats>) {
        chunk.items.forEach { stats ->
            dailyStatsRepository.deleteByStatDateAndChannelAndProgram(stats.statDate, stats.channel, stats.program)
            dailyStatsRepository.flush()
            dailyStatsRepository.save(stats)
        }
    }
}
