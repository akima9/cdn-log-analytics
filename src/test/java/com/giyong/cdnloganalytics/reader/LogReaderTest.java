package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.service.LogIngestService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogReaderTest {
    @Test
    void nothing() {
        assertTrue(true);
    }

    //파일을 읽고 LogIngestService.ingest 호출.
    @Test
    void should_call_ingest_for_line() throws IOException {
        //given
        LogIngestService logIngestService = mock(LogIngestService.class);

        Path path = Files.createTempFile("test", ".log");
        Files.write(path, List.of("line1"));

        LogReader logReader = new LogReader(logIngestService, path, CdnProvider.AWS);

        //when
        logReader.read();

        //then
        verify(logIngestService)
                .ingest("line1", CdnProvider.AWS);
    }

    //여러줄을 읽고 LogIngestService.ingest 호출.
    @Test
    void should_call_ingest_for_each_line() throws IOException {
        //given
        LogIngestService logIngestService = mock(LogIngestService.class);

        Path path = Files.createTempFile("test", ".log");
        Files.write(path, List.of("line1", "line2"));

        LogReader logReader = new LogReader(logIngestService, path, CdnProvider.AWS);

        //when
        logReader.read();

        //then
        verify(logIngestService, times(2))
                .ingest(anyString(), eq(CdnProvider.AWS));
    }

    //빈파일이면 LogIngestService.ingest() 호출안됨.
    @Test
    void should_not_call_ingest_when_empty_file() throws IOException {
        //given
        LogIngestService logIngestService = mock(LogIngestService.class);

        Path path = Files.createTempFile("test", ".log");
        Files.write(path, List.of());

        LogReader logReader = new LogReader(logIngestService, path, CdnProvider.AWS);

        //when
        logReader.read();

        //then
        verify(logIngestService, never())
                .ingest(any(), any());
    }
}