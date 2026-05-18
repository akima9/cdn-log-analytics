package com.example.cdn.mock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CloudFrontLogGeneratorTest {

    private lateinit var generator: CloudFrontLogGenerator

    @BeforeEach
    fun setUp() {
        generator = CloudFrontLogGenerator()
    }

    @Test
    fun `지정한 날짜로 로그 엔트리를 생성한다`() {
        val date = LocalDate.of(2026, 5, 18)

        val entries = generator.generate(date, 10)

        assertThat(entries).allMatch { it.date == date }
    }

    @Test
    fun `지정한 건수만큼 로그 엔트리를 생성한다`() {
        val entries = generator.generate(LocalDate.now(), 100)

        assertThat(entries).hasSize(100)
    }

    @Test
    fun `bytes는 양수이다`() {
        val entries = generator.generate(LocalDate.now(), 50)

        assertThat(entries).allMatch { it.bytes > 0 }
    }

    @Test
    fun `status는 유효한 HTTP 상태코드이다`() {
        val validStatuses = setOf(200, 206, 304, 400, 403, 404, 500)
        val entries = generator.generate(LocalDate.now(), 200)

        assertThat(entries).allMatch { it.status in validStatuses }
    }

    @Test
    fun `uriStem은 channelCode와 programCode를 포함한다`() {
        val entries = generator.generate(LocalDate.now(), 50)

        assertThat(entries).allMatch { it.uriStem.startsWith("/live/") }
        assertThat(entries).allMatch { it.uriStem.endsWith("/index.m3u8") }
    }

    @Test
    fun `edgeLocation은 유효한 CloudFront POP 코드이다`() {
        val validLocations = setOf("ICN50", "NRT20", "SIN6", "SYD1", "LAX1", "JFK51", "LHR62")
        val entries = generator.generate(LocalDate.now(), 100)

        assertThat(entries).allMatch { it.edgeLocation in validLocations }
    }
}
