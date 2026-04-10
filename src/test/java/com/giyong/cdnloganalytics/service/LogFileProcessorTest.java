package com.giyong.cdnloganalytics.service;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.reader.LogReader;
import com.giyong.cdnloganalytics.reader.LogReaderFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogFileProcessorTest {
    @Test
    void nothing() {
        assertTrue(true);
    }

    @Test
    void should_create_log_reader_and_read() {
        //given
        LogReaderFactory logReaderFactory = mock(LogReaderFactory.class);
        LogReader logReader = mock(LogReader.class);

        when(logReaderFactory.create(any(), any())).thenReturn(logReader);

        LogFileProcessor logFileProcessor = new LogFileProcessor(
                logReaderFactory,
                CdnProvider.AWS
        );

        //when
        logFileProcessor.accept("file.log");

        //then
        verify(logReaderFactory).create(Path.of("file.log"), CdnProvider.AWS);
        verify(logReader).read();
    }
}