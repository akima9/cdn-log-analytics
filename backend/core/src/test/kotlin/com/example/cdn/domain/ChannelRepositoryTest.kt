package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.junit.jupiter.api.assertThrows

@DataJpaTest
class ChannelRepositoryTest @Autowired constructor(
    private val channelRepository: ChannelRepository,
) {

    @Test
    fun `채널을 저장하고 ID로 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))

        val found = channelRepository.findById(channel.id)

        assertThat(found).isPresent
        assertThat(found.get().name).isEqualTo("뉴스 채널")
        assertThat(found.get().code).isEqualTo("NEWS")
    }

    @Test
    fun `채널 name은 유니크해야 한다`() {
        channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))

        assertThrows<DataIntegrityViolationException> {
            channelRepository.saveAndFlush(Channel(name = "뉴스 채널", code = "NEWS2"))
        }
    }

    @Test
    fun `채널 code는 유니크해야 한다`() {
        channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))

        assertThrows<DataIntegrityViolationException> {
            channelRepository.saveAndFlush(Channel(name = "뉴스 채널 2", code = "NEWS"))
        }
    }
}
