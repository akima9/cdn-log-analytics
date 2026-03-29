package com.giyong.cdnloganalytics.mapper;

import com.giyong.cdnloganalytics.dto.ParsedLog;
import com.giyong.cdnloganalytics.entity.RawLog;

import java.util.List;

public class LogMapper {
    public RawLog toEntity(ParsedLog parsedLog) {
        return RawLog.builder()
                .cdnProvider(parsedLog.getCdnProvider())
                .requestTime(parsedLog.getRequestTime())
                .channelId(parsedLog.getChannelId())
                .programId(parsedLog.getProgramId())
                .ip(parsedLog.getIp())
                .status(parsedLog.getStatus())
                .bytes(parsedLog.getBytes())
                .edgeLocation(parsedLog.getEdgeLocation())
                .build();
    }

    public List<RawLog> toEntities(List<ParsedLog> parsedLogs) {
        return parsedLogs.stream()
                .map(this::toEntity)
                .toList();
    }
}
