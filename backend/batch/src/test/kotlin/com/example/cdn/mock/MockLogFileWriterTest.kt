package com.example.cdn.mock

import com.example.cdn.config.MockProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.LocalDate
import kotlin.io.path.readText

class MockLogFileWriterTest {

    @Test
    fun `지정된 디렉터리에 파일이 생성된다`(@TempDir tempDir: Path) {
        val writer = MockLogFileWriter(CloudFrontLogGenerator(), MockProperties(outputDir = tempDir.toString()))

        writer.write(LocalDate.of(2026, 5, 18))

        assertThat(tempDir.toFile().listFiles()).isNotEmpty
    }

    @Test
    fun `파일명은 cloudfront_yyyyMMdd_log 형식이다`(@TempDir tempDir: Path) {
        val writer = MockLogFileWriter(CloudFrontLogGenerator(), MockProperties(outputDir = tempDir.toString()))

        writer.write(LocalDate.of(2026, 5, 18))

        val files = tempDir.toFile().listFiles()!!
        assertThat(files.map { it.name }).contains("cloudfront_20260518.log")
    }

    @Test
    fun `파일 내용은 CloudFront 헤더로 시작한다`(@TempDir tempDir: Path) {
        val writer = MockLogFileWriter(CloudFrontLogGenerator(), MockProperties(outputDir = tempDir.toString(), dailyCount = 5))

        writer.write(LocalDate.of(2026, 5, 18))

        val content = tempDir.resolve("cloudfront_20260518.log").readText()
        assertThat(content.lines().first()).isEqualTo("#Version: 1.0")
        assertThat(content.lines()[1]).startsWith("#Fields:")
    }
}
