package com.example.cdn.mock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class CloudFrontLogFormatterTest {

    private val sampleEntries = listOf(
        CloudFrontLogEntry(
            date = LocalDate.of(2026, 5, 18),
            time = LocalTime.of(10, 30, 0),
            edgeLocation = "ICN50",
            bytes = 524288L,
            clientIp = "1.2.3.4",
            method = "GET",
            host = "d1234abcd.cloudfront.net",
            uriStem = "/live/NEWS/MORNING_NEWS/index.m3u8",
            status = 200,
            userAgent = "Mozilla/5.0",
            resultType = "Hit",
            protocol = "https",
        )
    )

    @Test
    fun `첫 번째 줄은 #Version 1_0이다`() {
        val result = CloudFrontLogFormatter.format(sampleEntries)
        assertThat(result.lines().first()).isEqualTo("#Version: 1.0")
    }

    @Test
    fun `두 번째 줄은 #Fields로 시작한다`() {
        val result = CloudFrontLogFormatter.format(sampleEntries)
        assertThat(result.lines()[1]).startsWith("#Fields:")
    }

    @Test
    fun `로그 행은 탭으로 구분된다`() {
        val result = CloudFrontLogFormatter.format(sampleEntries)
        val logLine = result.lines()[2]
        assertThat(logLine).contains("\t")
        assertThat(logLine.split("\t")).hasSize(12)
    }

    @Test
    fun `날짜 포맷은 yyyy-MM-dd이다`() {
        val result = CloudFrontLogFormatter.format(sampleEntries)
        val logLine = result.lines()[2]
        assertThat(logLine).startsWith("2026-05-18")
    }

    @Test
    fun `시간 포맷은 HH_mm_ss이다`() {
        val result = CloudFrontLogFormatter.format(sampleEntries)
        val logLine = result.lines()[2]
        val fields = logLine.split("\t")
        assertThat(fields[1]).isEqualTo("10:30:00")
    }
}
