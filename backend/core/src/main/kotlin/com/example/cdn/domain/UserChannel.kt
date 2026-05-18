package com.example.cdn.domain

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_channels")
open class UserChannel(
    @EmbeddedId
    val id: UserChannelId,
    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne(fetch = LAZY)
    @MapsId("channelId")
    @JoinColumn(name = "channel_id")
    val channel: Channel,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
