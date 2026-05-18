package com.example.cdn.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "monthly_stats",
    uniqueConstraints = [UniqueConstraint(columnNames = ["stat_year", "stat_month", "channel_id", "program_id"])],
)
open class MonthlyStats(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    val statYear: Int,
    val statMonth: Int,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    val channel: Channel,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    val program: Program,
    val totalRequests: Long = 0,
    val totalBytes: Long = 0,
    val errorCount: Long = 0,
    @Column(precision = 20, scale = 2)
    val avgBytes: BigDecimal = BigDecimal.ZERO,
    val uniqueIps: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
