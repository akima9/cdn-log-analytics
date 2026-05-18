package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class CdnLogRepositoryTest @Autowired constructor(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val cdnLogRepository: CdnLogRepository,
) {

    @Test
    fun `채널과 프로그램과 요청 시간 범위로 로그를 조회할 수 있다`() {
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val program = programRepository.save(Program(channel = channel, name = "아침 뉴스", code = "MORNING_NEWS"))
        val base = LocalDateTime.of(2026, 5, 18, 10, 0, 0)
        cdnLogRepository.save(CdnLog(cdnProvider = "CloudFront", requestTime = base, channel = channel, program = program, ip = "1.2.3.4", status = 200, bytes = 1024, edgeLocation = "ICN50"))
        cdnLogRepository.save(CdnLog(cdnProvider = "CloudFront", requestTime = base.plusHours(2), channel = channel, program = program, ip = "1.2.3.5", status = 200, bytes = 2048, edgeLocation = "ICN50"))

        val logs = cdnLogRepository.findByChannelAndProgramAndRequestTimeBetween(channel, program, base, base.plusHours(1))

        assertThat(logs).hasSize(1)
        assertThat(logs[0].ip).isEqualTo("1.2.3.4")
    }
}
