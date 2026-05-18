package com.example.cdn.step

import com.example.cdn.domain.CdnLog
import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.ProgramRepository
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class CloudFrontLogProcessor(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
) : ItemProcessor<CloudFrontLogRecord, CdnLog> {

    override fun process(record: CloudFrontLogRecord): CdnLog? {
        val (channelCode, programCode) = parseUri(record.uriStem) ?: return null
        val channel = channelRepository.findByCode(channelCode) ?: return null
        val program = programRepository.findByChannelAndCode(channel, programCode) ?: return null

        return CdnLog(
            cdnProvider = "CloudFront",
            requestTime = LocalDateTime.of(LocalDate.parse(record.date), LocalTime.parse(record.time)),
            channel = channel,
            program = program,
            ip = record.clientIp,
            status = record.status.toShort(),
            bytes = record.bytes.toLong(),
            edgeLocation = record.edgeLocation,
        )
    }

    // URI: /live/{channelCode}/{programCode}/index.m3u8
    private fun parseUri(uriStem: String): Pair<String, String>? {
        val parts = uriStem.split("/")
        if (parts.size < 5) return null
        return parts[2] to parts[3]
    }
}
