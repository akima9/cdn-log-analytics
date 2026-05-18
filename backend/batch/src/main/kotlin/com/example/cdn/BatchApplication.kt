package com.example.cdn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import com.example.cdn.config.MockProperties

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(MockProperties::class)
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}
