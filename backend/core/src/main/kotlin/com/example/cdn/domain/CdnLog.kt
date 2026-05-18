package com.example.cdn.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "cdn_logs")
open class CdnLog(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    val cdnProvider: String,
    val requestTime: LocalDateTime,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    val channel: Channel,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    val program: Program,
    val ip: String,
    val status: Short,
    val bytes: Long,
    val edgeLocation: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
