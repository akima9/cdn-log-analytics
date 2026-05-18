package com.example.cdn.step

import com.example.cdn.domain.Channel
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.Program
import com.example.cdn.domain.ProgramRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CloudFrontLogProcessorTest {

    private val channelRepository = mockk<ChannelRepository>()
    private val programRepository = mockk<ProgramRepository>()
    private val processor = CloudFrontLogProcessor(channelRepository, programRepository)

    private val channel = Channel(id = 1L, name = "뉴스 채널", code = "NEWS")
    private val program = Program(id = 1L, channel = channel, name = "아침 뉴스", code = "MORNING_NEWS")

    private fun record(uriStem: String = "/live/NEWS/MORNING_NEWS/index.m3u8") = CloudFrontLogRecord(
        date = "2025-05-18",
        time = "01:23:45",
        edgeLocation = "ICN50",
        bytes = "123456",
        clientIp = "192.168.1.1",
        method = "GET",
        host = "d123.cloudfront.net",
        uriStem = uriStem,
        status = "200",
        userAgent = "Mozilla/5.0",
        resultType = "Hit",
        protocol = "https",
    )

    @Test
    fun `유효한 레코드를 CdnLog로 변환한다`() {
        every { channelRepository.findByCode("NEWS") } returns channel
        every { programRepository.findByChannelAndCode(channel, "MORNING_NEWS") } returns program

        val result = processor.process(record())!!

        assertEquals("CloudFront", result.cdnProvider)
        assertEquals("ICN50", result.edgeLocation)
        assertEquals(123456L, result.bytes)
        assertEquals(200.toShort(), result.status)
        assertEquals("192.168.1.1", result.ip)
        assertEquals(channel, result.channel)
        assertEquals(program, result.program)
    }

    @Test
    fun `존재하지 않는 채널이면 null을 반환한다`() {
        every { channelRepository.findByCode("NEWS") } returns null

        assertNull(processor.process(record()))
    }

    @Test
    fun `존재하지 않는 프로그램이면 null을 반환한다`() {
        every { channelRepository.findByCode("NEWS") } returns channel
        every { programRepository.findByChannelAndCode(channel, "MORNING_NEWS") } returns null

        assertNull(processor.process(record()))
    }

    @Test
    fun `URI 형식이 잘못되면 null을 반환한다`() {
        assertNull(processor.process(record(uriStem = "/invalid")))
    }
}
