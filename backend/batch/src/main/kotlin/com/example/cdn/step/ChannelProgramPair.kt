package com.example.cdn.step

import com.example.cdn.domain.Channel
import com.example.cdn.domain.Program

data class ChannelProgramPair(
    val channel: Channel,
    val program: Program,
)
