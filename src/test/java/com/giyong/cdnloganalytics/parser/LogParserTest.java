package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.dto.ParsedLog;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class LogParserTest {
    protected abstract LogParser createLogParser();
    protected abstract String validLogLine();
    protected abstract Map<String, Integer> fieldIndex();

    @Test
    void should_parse_valid_log() {
        //given
        LogParser logParser = createLogParser();

        //when
        ParsedLog parsedLog = logParser.parse(validLogLine(), fieldIndex());

        //then
        assertAll(
                () -> assertNotNull(parsedLog),
                () -> assertNotNull(parsedLog.getIp()),
                () -> assertNotNull(parsedLog.getBytes()),
                () -> assertTrue(parsedLog.getStatus() > 0),
                () -> assertNotNull(parsedLog.getCdnProvider()),
                () -> assertNotNull(parsedLog.getChannelId()),
                () -> assertNotNull(parsedLog.getEdgeLocation()),
                () -> assertNotNull(parsedLog.getProgramId()),
                () -> assertNotNull(parsedLog.getRequestTime())
        );
    }

    @Test
    void should_throw_exception_for_invalid_log() {
        LogParser logParser = createLogParser();

        assertThrows(Exception.class, () -> {
            logParser.parse("invalid log", fieldIndex());
        });
    }
}