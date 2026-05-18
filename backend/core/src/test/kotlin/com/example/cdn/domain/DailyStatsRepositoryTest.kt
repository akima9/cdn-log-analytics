package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate

@DataJpaTest
class DailyStatsRepositoryTest @Autowired constructor(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val dailyStatsRepository: DailyStatsRepository,
) {

    @Test
    fun `일별 통계를 저장하고 날짜 범위와 채널로 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        dailyStatsRepository.save(DailyStats(statDate = LocalDate.of(2026, 5, 17), channel = channel, program = program))
        dailyStatsRepository.save(DailyStats(statDate = LocalDate.of(2026, 5, 18), channel = channel, program = program))

        val results = dailyStatsRepository.findByStatDateBetweenAndChannelIn(
            LocalDate.of(2026, 5, 18), LocalDate.of(2026, 5, 18), listOf(channel)
        )

        assertThat(results).hasSize(1)
        assertThat(results[0].statDate).isEqualTo(LocalDate.of(2026, 5, 18))
    }

    @Test
    fun `같은 날짜와 채널과 프로그램 조합은 유니크해야 한다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        dailyStatsRepository.save(DailyStats(statDate = LocalDate.of(2026, 5, 18), channel = channel, program = program))

        assertThrows<DataIntegrityViolationException> {
            dailyStatsRepository.saveAndFlush(DailyStats(statDate = LocalDate.of(2026, 5, 18), channel = channel, program = program))
        }
    }
}
