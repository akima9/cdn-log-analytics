package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.dto.ParsedLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class LogParserTest {
    protected abstract LogParser createLogParser();
    protected abstract String validLogLine();
    protected abstract String validFieldLine();

    @Test
    void should_return_null_when_field() {
        //given
        LogParser logParser = createLogParser();

        //when
        ParsedLog parsedLog = logParser.parse(validFieldLine());

        //then
        assertNull(parsedLog);
    }

    @Test
    void should_parse_valid_log() {
        //given
        LogParser logParser = createLogParser();
        logParser.parse(validFieldLine());

        //when
        ParsedLog parsedLog = logParser.parse(validLogLine());

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
            logParser.parse("invalid log");
        });
    }
}