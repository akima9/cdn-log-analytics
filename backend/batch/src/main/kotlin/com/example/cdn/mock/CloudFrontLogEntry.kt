package com.example.cdn.mock

import java.time.LocalDate
import java.time.LocalTime

data class CloudFrontLogEntry(
    val date: LocalDate,
    val time: LocalTime,
    val edgeLocation: String,
    val bytes: Long,
    val clientIp: String,
    val method: String,
    val host: String,
    val uriStem: String,
    val status: Int,
    val userAgent: String,
    val resultType: String,
    val protocol: String,
)
