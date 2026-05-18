package com.example.cdn.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UserChannelRepository : JpaRepository<UserChannel, UserChannelId> {
    fun findByUser(user: User): List<UserChannel>
    fun deleteByUser(user: User)
}
