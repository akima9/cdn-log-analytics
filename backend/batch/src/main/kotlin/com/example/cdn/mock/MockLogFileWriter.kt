package com.example.cdn.mock

import com.example.cdn.config.MockProperties
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class MockLogFileWriter(
    private val generator: CloudFrontLogGenerator,
    private val properties: MockProperties,
) {

    private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    fun write(date: LocalDate) {
        val outputDir = Path.of(properties.outputDir)
        Files.createDirectories(outputDir)

        val entries = generator.generate(date, properties.dailyCount)
        val content = CloudFrontLogFormatter.format(entries)

        val fileName = "cloudfront_${date.format(fileNameFormatter)}.log"
        Files.writeString(outputDir.resolve(fileName), content)
    }
}
