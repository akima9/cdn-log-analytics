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
import java.time.LocalDateTime

@Entity
@Table(
    name = "programs",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["channel_id", "name"]),
        UniqueConstraint(columnNames = ["channel_id", "code"]),
    ],
)
open class Program(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    val channel: Channel,
    val name: String,
    val code: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
