package com.example.cdn.step

import com.example.cdn.domain.ChannelRepository
import com.example.cdn.domain.ProgramRepository
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader

open class ChannelProgramReader(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
) : ItemStreamReader<ChannelProgramPair> {

    private var items: Iterator<ChannelProgramPair> = emptyList<ChannelProgramPair>().iterator()

    override fun open(executionContext: ExecutionContext) {
        items = channelRepository.findAll()
            .flatMap { channel -> programRepository.findByChannel(channel).map { ChannelProgramPair(channel, it) } }
            .iterator()
    }

    override fun read(): ChannelProgramPair? = if (items.hasNext()) items.next() else null

    override fun update(executionContext: ExecutionContext) {}
    override fun close() {}
}
