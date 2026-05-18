package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface CdnLogRepository : JpaRepository<CdnLog, Long> {
    fun findByChannelAndProgramAndRequestTimeBetween(
        channel: Channel,
        program: Program,
        start: LocalDateTime,
        end: LocalDateTime,
    ): List<CdnLog>
}
