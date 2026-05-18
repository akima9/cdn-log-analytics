package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyStatsRepository : JpaRepository<DailyStats, Long> {
    fun findByStatDateBetweenAndChannelIn(
        startDate: LocalDate,
        endDate: LocalDate,
        channels: List<Channel>,
    ): List<DailyStats>
}
