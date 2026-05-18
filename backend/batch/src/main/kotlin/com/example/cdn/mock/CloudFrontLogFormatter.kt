package com.example.cdn.mock

import java.time.format.DateTimeFormatter

object CloudFrontLogFormatter {

    private val DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss")

    private const val VERSION_HEADER = "#Version: 1.0"
    private const val FIELDS_HEADER =
        "#Fields: date time x-edge-location sc-bytes c-ip cs-method cs(Host) cs-uri-stem sc-status cs(User-Agent) x-edge-result-type cs-protocol"

    fun format(entries: List<CloudFrontLogEntry>): String = buildString {
        appendLine(VERSION_HEADER)
        appendLine(FIELDS_HEADER)
        entries.forEach { entry ->
            appendLine(formatLine(entry))
        }
    }.trimEnd()

    private fun formatLine(entry: CloudFrontLogEntry): String = listOf(
        entry.date.format(DATE_FMT),
        entry.time.format(TIME_FMT),
        entry.edgeLocation,
        entry.bytes.toString(),
        entry.clientIp,
        entry.method,
        entry.host,
        entry.uriStem,
        entry.status.toString(),
        entry.userAgent,
        entry.resultType,
        entry.protocol,
    ).joinToString("\t")
}
