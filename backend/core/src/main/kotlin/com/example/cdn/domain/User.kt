package com.example.cdn.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
open class User(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    val email: String,
    val passwordHash: String,
    @Enumerated(STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    val role: UserRole = UserRole.USER,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
