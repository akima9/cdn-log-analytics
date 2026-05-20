package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class MonthlyStatsRepositoryTest @Autowired constructor(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val monthlyStatsRepository: MonthlyStatsRepository,
) {

    @Test
    fun `월별 통계를 저장하고 연월 범위와 채널로 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        monthlyStatsRepository.save(MonthlyStats(statYear = 2026, statMonth = 4, channel = channel, program = program))
        monthlyStatsRepository.save(MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = program))

        val results = monthlyStatsRepository.findByStatYearAndStatMonthBetweenAndChannelIn(
            statYear = 2026, startMonth = 5, endMonth = 5, channels = listOf(channel)
        )

        assertThat(results).hasSize(1)
        assertThat(results[0].statMonth).isEqualTo(5)
    }

    @Test
    fun `연도와 월과 채널과 프로그램으로 월별 통계를 삭제할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        val other = programRepository.save(Program(channel = channel, name = "저녁 뉴스", code = "EVENING_NEWS"))
        monthlyStatsRepository.save(MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = program))
        monthlyStatsRepository.save(MonthlyStats(statYear = 2026, statMonth = 5, channel = channel, program = other))

        monthlyStatsRepository.deleteByStatYearAndStatMonthAndChannelAndProgram(2026, 5, channel, program)
        monthlyStatsRepository.flush()

        assertThat(monthlyStatsRepository.count()).isEqualTo(1)
    }
}
