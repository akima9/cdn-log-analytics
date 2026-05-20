package com.example.cdn.step

import com.example.cdn.domain.Channel
import com.example.cdn.domain.DailyStats
import com.example.cdn.domain.DailyStatsRepository
import com.example.cdn.domain.Program
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifyOrder
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk
import java.time.LocalDate

class DailyStatsWriterTest {

    private val dailyStatsRepository = mockk<DailyStatsRepository>()
    private val writer = DailyStatsWriter(dailyStatsRepository)

    private val channel = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val program = Program(id = 1L, channel = channel, name = "아침 뉴스", code = "MORNING_NEWS")
    private val statDate = LocalDate.of(2026, 5, 18)

    @Test
    fun `저장 전에 기존 데이터를 삭제하고 flush한 후 저장한다`() {
        val stats = DailyStats(statDate = statDate, channel = channel, program = program)
        every { dailyStatsRepository.deleteByStatDateAndChannelAndProgram(any(), any(), any()) } just Runs
        every { dailyStatsRepository.flush() } just Runs
        every { dailyStatsRepository.save(any()) } answers { firstArg() }

        writer.write(Chunk(listOf(stats)))

        verifyOrder {
            dailyStatsRepository.deleteByStatDateAndChannelAndProgram(statDate, channel, program)
            dailyStatsRepository.flush()
            dailyStatsRepository.save(stats)
        }
    }

    @Test
    fun `여러 아이템을 각각 delete-flush-save 순서로 처리한다`() {
        val other = Program(id = 2L, channel = channel, name = "저녁 뉴스", code = "EVENING_NEWS")
        val stats1 = DailyStats(statDate = statDate, channel = channel, program = program)
        val stats2 = DailyStats(statDate = statDate, channel = channel, program = other)
        every { dailyStatsRepository.deleteByStatDateAndChannelAndProgram(any(), any(), any()) } just Runs
        every { dailyStatsRepository.flush() } just Runs
        every { dailyStatsRepository.save(any()) } answers { firstArg() }

        writer.write(Chunk(listOf(stats1, stats2)))

        verify(exactly = 2) { dailyStatsRepository.deleteByStatDateAndChannelAndProgram(any(), any(), any()) }
        verify(exactly = 2) { dailyStatsRepository.flush() }
        verify(exactly = 2) { dailyStatsRepository.save(any()) }
    }
}
