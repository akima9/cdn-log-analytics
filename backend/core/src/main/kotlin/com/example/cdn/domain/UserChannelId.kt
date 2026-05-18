package com.example.cdn.domain

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class UserChannelId(
    val userId: Long = 0,
    val channelId: Long = 0,
) : Serializable
