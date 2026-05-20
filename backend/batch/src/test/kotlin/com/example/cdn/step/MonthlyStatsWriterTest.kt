package com.example.cdn.step

import com.example.cdn.domain.Channel
import com.example.cdn.domain.MonthlyStats
import com.example.cdn.domain.MonthlyStatsRepository
import com.example.cdn.domain.Program
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk

class MonthlyStatsWriterTest {

    private val monthlyStatsRepository = mockk<MonthlyStatsRepository>()
    private val writer = MonthlyStatsWriter(monthlyStatsRepository)

    private val channel = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val program = Program(id = 1L, channel = channel, name = "아침 뉴스", code = "MORNING_NEWS")

    @Test
    fun `저장 전에 기존 데이터를 삭제하고 flush한 후 저장한다`() {
        val stats = MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = program)
        every { monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(any(), any(), any(), any()) } just Runs
        every { monthlyStatsRepository.flush() } just Runs
        every { monthlyStatsRepository.save(any()) } answers { firstArg() }

        writer.write(Chunk(listOf(stats)))

        verifyOrder {
            monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(2026, 5, channel, program)
            monthlyStatsRepository.flush()
            monthlyStatsRepository.save(stats)
        }
    }

    @Test
    fun `여러 아이템을 각각 delete-flush-save 순서로 처리한다`() {
        val other = Program(id = 2L, channel = channel, name = "저녁 뉴스", code = "EVENING_NEWS")
        val stats1 = MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = program)
        val stats2 = MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = other)
        every { monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(any(), any(), any(), any()) } just Runs
        every { monthlyStatsRepository.flush() } just Runs
        every { monthlyStatsRepository.save(any()) } answers { firstArg() }

        writer.write(Chunk(listOf(stats1, stats2)))

        verify(exactly = 2) { monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(any(), any(), any(), any()) }
        verify(exactly = 2) { monthlyStatsRepository.flush() }
        verify(exactly = 2) { monthlyStatsRepository.save(any()) }
    }
}
