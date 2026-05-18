package com.example.cdn.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "channels")
open class Channel(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    val name: String,
    @Column(unique = true)
    val code: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
