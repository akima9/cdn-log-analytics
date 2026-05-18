package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ChannelRepository : JpaRepository<Channel, Long> {
    fun findByCode(code: String): Channel?
}
