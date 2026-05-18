package com.example.cdn.mock

import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class CloudFrontLogGenerator(private val random: Random = Random.Default) {

    companion object {
        private val CHANNEL_PROGRAMS = mapOf(
            "NEWS" to listOf("MORNING_NEWS", "EVENING_NEWS", "NIGHT_NEWS"),
            "SPORTS" to listOf("LIVE_SOCCER", "LIVE_BASEBALL", "SPORTS_HIGHLIGHT"),
            "ENTERTAINMENT" to listOf("VARIETY_SHOW", "DRAMA_A", "DRAMA_B"),
            "CULTURE" to listOf("DOCUMENTARY", "COOKING", "TRAVEL"),
        )
        private val CHANNEL_CODES = CHANNEL_PROGRAMS.keys.toList()
        private val EDGE_LOCATIONS = listOf("ICN50", "NRT20", "SIN6", "SYD1", "LAX1", "JFK51", "LHR62")
        private val STATUS_CODES = listOf(200, 206, 304, 400, 403, 404, 500)
        private val STATUS_WEIGHTS = listOf(60, 20, 5, 5, 3, 5, 2) // 총합 100
        private val RESULT_TYPES = listOf("Hit", "Miss", "RefreshHit", "Error")
        private const val HOST = "d1a2b3c4d5e6.cloudfront.net"
        private val USER_AGENTS = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15",
            "HLS-Player/3.0 (Linux; Android 13)",
        )
    }

    fun generate(date: LocalDate, count: Int): List<CloudFrontLogEntry> =
        (1..count).map { buildEntry(date) }

    private fun buildEntry(date: LocalDate): CloudFrontLogEntry {
        val channelCode = CHANNEL_CODES[random.nextInt(CHANNEL_CODES.size)]
        val programCode = CHANNEL_PROGRAMS.getValue(channelCode).let { it[random.nextInt(it.size)] }
        val status = weightedRandom(STATUS_CODES, STATUS_WEIGHTS)

        return CloudFrontLogEntry(
            date = date,
            time = randomTime(),
            edgeLocation = EDGE_LOCATIONS[random.nextInt(EDGE_LOCATIONS.size)],
            bytes = (random.nextLong(512 * 1024) + 1024), // 1KB ~ 512KB
            clientIp = randomIp(),
            method = "GET",
            host = HOST,
            uriStem = "/live/$channelCode/$programCode/index.m3u8",
            status = status,
            userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.size)],
            resultType = if (status >= 400) "Error" else RESULT_TYPES[random.nextInt(3)],
            protocol = "https",
        )
    }

    private fun randomTime(): LocalTime =
        LocalTime.of(
            random.nextInt(24),
            random.nextInt(60),
            random.nextInt(60),
        )

    private fun randomIp(): String =
        "${random.nextInt(1, 255)}.${random.nextInt(256)}.${random.nextInt(256)}.${random.nextInt(1, 255)}"

    private fun <T> weightedRandom(items: List<T>, weights: List<Int>): T {
        val total = weights.sum()
        var threshold = random.nextInt(total)
        for (i in items.indices) {
            threshold -= weights[i]
            if (threshold < 0) return items[i]
        }
        return items.last()
    }
}
