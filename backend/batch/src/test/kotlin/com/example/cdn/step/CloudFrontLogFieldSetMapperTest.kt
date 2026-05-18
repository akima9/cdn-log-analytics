package com.example.cdn.step

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.batch.item.file.transform.DefaultFieldSet

class CloudFrontLogFieldSetMapperTest {

    private val names = arrayOf(
        "date", "time", "edgeLocation", "bytes", "clientIp", "method",
        "host", "uriStem", "status", "userAgent", "resultType", "protocol",
    )

    private fun fieldSet(vararg tokens: String) = DefaultFieldSet(arrayOf(*tokens), names)

    @Test
    fun `탭으로 구분된 로그 라인을 CloudFrontLogRecord로 변환한다`() {
        val fs = fieldSet(
            "2025-05-18", "01:23:45", "ICN50", "123456", "192.168.1.1",
            "GET", "d123.cloudfront.net", "/live/NEWS/MORNING_NEWS/index.m3u8",
            "200", "Mozilla/5.0", "Hit", "https",
        )

        val record = CloudFrontLogFieldSetMapper().mapFieldSet(fs)

        assertEquals("2025-05-18", record.date)
        assertEquals("01:23:45", record.time)
        assertEquals("ICN50", record.edgeLocation)
        assertEquals("123456", record.bytes)
        assertEquals("192.168.1.1", record.clientIp)
        assertEquals("GET", record.method)
        assertEquals("d123.cloudfront.net", record.host)
        assertEquals("/live/NEWS/MORNING_NEWS/index.m3u8", record.uriStem)
        assertEquals("200", record.status)
        assertEquals("Mozilla/5.0", record.userAgent)
        assertEquals("Hit", record.resultType)
        assertEquals("https", record.protocol)
    }

    @Test
    fun `날짜·시간·URI 필드가 올바른 위치에 매핑된다`() {
        val fs = fieldSet(
            "2025-12-31", "23:59:59", "NRT20", "999", "10.0.0.1",
            "GET", "d456.cloudfront.net", "/live/SPORTS/LIVE_SOCCER/index.m3u8",
            "206", "curl/7.0", "Miss", "https",
        )

        val record = CloudFrontLogFieldSetMapper().mapFieldSet(fs)

        assertEquals("2025-12-31", record.date)
        assertEquals("23:59:59", record.time)
        assertEquals("/live/SPORTS/LIVE_SOCCER/index.m3u8", record.uriStem)
    }
}
