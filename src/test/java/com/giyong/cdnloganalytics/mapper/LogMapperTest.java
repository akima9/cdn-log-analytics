package com.giyong.cdnloganalytics.mapper;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import com.giyong.cdnloganalytics.entity.RawLog;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogMapperTest {
    private final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    void should_convert_parsed_log_to_raw_log() {
        //given
        ParsedLog parsedLog = createParsedLog(
                CdnProvider.AWS,
                "2026-03-27 23:22",
                0L,
                0L,
                "000.000.000.000",
                200,
                100L,
                "ICN1"
        );
        LogMapper logMapper = new LogMapper();

        //when
        RawLog rawLog = logMapper.toEntity(parsedLog);

        //then
        assertMapped(parsedLog, rawLog);
    }

    @Test
    void should_convert_parsed_log_list_to_raw_log_list() {
        //given
        ParsedLog parsedLog1 = createParsedLog(
                CdnProvider.AKAMAI,
                "2026-03-26 23:22",
                1L,
                1L,
                "111.000.000.000",
                500,
                101L,
                "ICN2"
        );
        ParsedLog parsedLog2 = createParsedLog(
                CdnProvider.AWS,
                "2026-03-27 23:22",
                0L,
                0L,
                "000.000.000.000",
                200,
                100L,
                "ICN1"
        );
        List<ParsedLog> parsedLogs = List.of(
                parsedLog1,
                parsedLog2
        );
        LogMapper logMapper = new LogMapper();

        //when
        List<RawLog> rawLogs = logMapper.toEntities(parsedLogs);

        //then
        assertAll(
                () -> assertEquals(2, rawLogs.size()),
                () -> assertMapped(parsedLog1, rawLogs.get(0)),
                () -> assertMapped(parsedLog2, rawLogs.get(1))
        );
    }

    private void assertMapped(ParsedLog parsedLog, RawLog rawLog) {
        assertAll(
                () -> assertEquals(parsedLog.getBytes(), rawLog.getBytes()),
                () -> assertEquals(parsedLog.getIp(), rawLog.getIp()),
                () -> assertEquals(parsedLog.getCdnProvider(), rawLog.getCdnProvider()),
                () -> assertEquals(parsedLog.getStatus(), rawLog.getStatus()),
                () -> assertEquals(parsedLog.getEdgeLocation(), rawLog.getEdgeLocation()),
                () -> assertEquals(parsedLog.getChannelId(), rawLog.getChannelId()),
                () -> assertEquals(parsedLog.getProgramId(), rawLog.getProgramId()),
                () -> assertEquals(parsedLog.getRequestTime(), rawLog.getRequestTime())
        );
    }

    private ParsedLog createParsedLog(
            CdnProvider cdnProvider,
            String requestTime,
            Long channelId,
            Long programId,
            String ip,
            int status,
            Long bytes,
            String edgeLocation
    ) {
        return ParsedLog.builder()
                .cdnProvider(cdnProvider)
                .requestTime(parseRequestTime(requestTime))
                .channelId(channelId)
                .programId(programId)
                .ip(ip)
                .status(status)
                .bytes(bytes)
                .edgeLocation(edgeLocation)
                .build();
    }

    private Instant parseRequestTime(String requestTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(requestTime, FORMATTER);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}