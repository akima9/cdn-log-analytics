package com.example.cdn.step

import com.example.cdn.domain.Channel
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.Program
import com.example.cdn.domain.ProgramRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.item.ExecutionContext

class ChannelProgramReaderTest {

    private val channelRepository = mockk<ChannelRepository>()
    private val programRepository = mockk<ProgramRepository>()

    private val channel1 = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val channel2 = Channel(id = 2L, name = "스포츠 채널", code = "SPORTS")
    private val prog1 = Program(id = 1L, channel = channel1, name = "아침 뉴스", code = "MORNING_NEWS")
    private val prog2 = Program(id = 2L, channel = channel1, name = "저녁 뉴스", code = "EVENING_NEWS")
    private val prog3 = Program(id = 3L, channel = channel2, name = "축구", code = "SOCCER")

    @Test
    fun `모든 채널과 프로그램 조합을 순서대로 읽는다`() {
        every { channelRepository.findAll() } returns listOf(channel1, channel2)
        every { programRepository.findByChannel(channel1) } returns listOf(prog1, prog2)
        every { programRepository.findByChannel(channel2) } returns listOf(prog3)

        val reader = ChannelProgramReader(channelRepository, programRepository)
        reader.open(ExecutionContext())

        val items = generateSequence { reader.read() }.toList()

        assertThat(items).hasSize(3)
        assertThat(items[0]).isEqualTo(ChannelProgramPair(channel1, prog1))
        assertThat(items[1]).isEqualTo(ChannelProgramPair(channel1, prog2))
        assertThat(items[2]).isEqualTo(ChannelProgramPair(channel2, prog3))
    }

    @Test
    fun `프로그램이 없는 채널은 아이템을 생성하지 않는다`() {
        every { channelRepository.findAll() } returns listOf(channel1)
        every { programRepository.findByChannel(channel1) } returns emptyList()

        val reader = ChannelProgramReader(channelRepository, programRepository)
        reader.open(ExecutionContext())

        assertThat(reader.read()).isNull()
    }

    @Test
    fun `모든 아이템을 읽은 후 null을 반환한다`() {
        every { channelRepository.findAll() } returns listOf(channel1)
        every { programRepository.findByChannel(channel1) } returns listOf(prog1)

        val reader = ChannelProgramReader(channelRepository, programRepository)
        reader.open(ExecutionContext())
        reader.read()

        assertThat(reader.read()).isNull()
    }
}
