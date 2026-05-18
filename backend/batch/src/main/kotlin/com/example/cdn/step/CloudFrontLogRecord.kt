package com.example.cdn.step

data class CloudFrontLogRecord(
    val date: String = "",
    val time: String = "",
    val edgeLocation: String = "",
    val bytes: String = "",
    val clientIp: String = "",
    val method: String = "",
    val host: String = "",
    val uriStem: String = "",
    val status: String = "",
    val userAgent: String = "",
    val resultType: String = "",
    val protocol: String = "",
)
