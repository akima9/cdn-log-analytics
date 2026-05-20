package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface MonthlyStatsRepository : JpaRepository<MonthlyStats, Long> {
    @Query(
        """
        SELECT m FROM MonthlyStats m
        WHERE m.statYear = :statYear
          AND m.statMonth BETWEEN :startMonth AND :endMonth
          AND m.channel IN :channels
        """
    )
    fun findByStatYearAndStatMonthBetweenAndChannelIn(
        statYear: Int,
        startMonth: Int,
        endMonth: Int,
        channels: List<Channel>,
    ): List<MonthlyStats>

    @Modifying
    fun deleteByStatYearAndStatMonthAndChannelAndProgram(
        statYear: Int,
        statMonth: Int,
        channel: Channel,
        program: Program,
    )
}
