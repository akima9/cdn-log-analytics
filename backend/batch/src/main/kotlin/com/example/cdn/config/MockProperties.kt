package com.example.cdn.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cdn.mock")
data class MockProperties(
    val outputDir: String = "/tmp/cdn-mock-logs",
    val dailyCount: Int = 10_000,
)
