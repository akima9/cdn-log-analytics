package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class ProgramRepositoryTest @Autowired constructor(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
) {

    @Test
    fun `프로그램을 채널과 함께 저장하고 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))

        val found = programRepository.findById(program.id)

        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("아침 뉴스")
        assertThat(found.get().channel.id).isEqualTo(channel.id)
    }

    @Test
    fun `같은 채널 내에서 프로그램 name은 유니크해야 한다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))

        assertThrows<DataIntegrityViolationException> {
            programRepository.saveAndFlush(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS2"))
        }
    }

    @Test
    fun `채널과 code로 프로그램을 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))

        val found = programRepository.findByChannelAndCode(channel, "MORNING_NEWS")

        assertThat(found).isNotNull
        assertThat(found!!.name).isEqualTo("아침 뉴스")
    }

    @Test
    fun `채널로 해당 채널의 프로그램 목록을 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val other = channelRepository.save(Channel(name = "스포츠 채널", code = "SPORTS"))
        programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        programRepository.save(Program(channel = channel, name = "저녁 뉴스", code = "EVENING_NEWS"))
        programRepository.save(Program(channel = other, name = "축구", code = "SOCCER"))

        val result = programRepository.findByChannel(channel)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.code }).containsExactlyInAnyOrder("MORNING_NEWS", "EVENING_NEWS")
    }
}
