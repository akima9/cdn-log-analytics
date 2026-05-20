package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<Program, Long> {
    fun findByChannelAndCode(channel: Channel, code: String): Program?
    fun findByChannel(channel: Channel): List<Program>
}
