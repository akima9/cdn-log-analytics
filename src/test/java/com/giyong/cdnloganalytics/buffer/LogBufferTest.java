package com.giyong.cdnloganalytics.buffer;

import com.giyong.cdnloganalytics.config.LogBufferProperties;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LogBufferTest {
    private LogBuffer createBuffer(int size) {
        LogBufferProperties logBufferProperties = new LogBufferProperties();
        logBufferProperties.setSize(size);
        return new LogBuffer(logBufferProperties);
    }

    @Test
    void should_add_one_parsed_log_then_buffer_size_one() {
        //given
        LogBuffer logBuffer = createBuffer(3);
        ParsedLog parsedLog = mock(ParsedLog.class);

        //when
        logBuffer.add(parsedLog);

        //then
        assertEquals(1, logBuffer.getSize());
    }

    @Test
    void should_add_two_parsed_log_then_buffer_size_two() {
        //given
        LogBuffer logBuffer = createBuffer(3);
        ParsedLog parsedLog1 = mock(ParsedLog.class);
        ParsedLog parsedLog2 = mock(ParsedLog.class);

        //when
        logBuffer.add(parsedLog1);
        logBuffer.add(parsedLog2);

        //then
        assertEquals(2, logBuffer.getSize());
    }

    @Test
    void should_not_flush_when_buffer_not_full() {
        //given
        LogBuffer logBuffer = createBuffer(3);
        ParsedLog parsedLog = mock(ParsedLog.class);

        //when
        boolean result = logBuffer.add(parsedLog);

        //then
        assertFalse(result);
    }

    @Test
    void should_flush_when_buffer_reaches_capacity() {
        List<ParsedLog> parsedLogs = null;

        //given
        LogBuffer logBuffer = createBuffer(3);

        for (int i = 0; i < 3; i++) {
            ParsedLog parsedLog = mock(ParsedLog.class);
            //when
            if (logBuffer.add(parsedLog)) {
                parsedLogs = logBuffer.flush();
            }
        }

        //then
        assertEquals(0, logBuffer.getSize());
        assertNotNull(parsedLogs);
        assertEquals(3, parsedLogs.size());
    }
}