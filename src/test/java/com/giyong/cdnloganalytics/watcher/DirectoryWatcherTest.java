package com.giyong.cdnloganalytics.watcher;

import com.giyong.cdnloganalytics.service.LogFileProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectoryWatcherTest {
    @Test
    void nothing() {
        assertTrue(true);
    }

    //파일이 감지되면 Consumer가 호출된다.
    @Test
    void should_call_consumer_when_new_file_detected() {
        //given
        FileSystemWatcher fileSystemWatcher = mock(FileSystemWatcher.class);
        FileDetector fileDetector = mock(FileDetector.class);
        LogFileProcessor logFileProcessor = mock(LogFileProcessor.class);

        when(fileSystemWatcher.pollEvents()).thenReturn(List.of("file.log"));
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(
                fileSystemWatcher,
                fileDetector,
                logFileProcessor
        );

        //when
        directoryWatcher.watch();

        //then
        verify(logFileProcessor).accept("file.log");
    }

    //파일이 여러개 감지되면 모두 처리한다.
    @Test
    void should_process_all_detected_files() {
        //given
        FileSystemWatcher fileSystemWatcher = mock(FileSystemWatcher.class);
        FileDetector fileDetector = mock(FileDetector.class);
        LogFileProcessor logFileProcessor = mock(LogFileProcessor.class);

        when(fileSystemWatcher.pollEvents()).thenReturn(
                List.of("file1.log", "file2.log")
        );

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(
                fileSystemWatcher,
                fileDetector,
                logFileProcessor
        );

        //when
        directoryWatcher.watch();

        //then
        verify(logFileProcessor).accept("file1.log");
        verify(logFileProcessor).accept("file2.log");
    }

    //파일 생성이 없으면 아무것도 안함.
    @Test
    void should_nothing_when_no_files_detected() {
        //given
        FileSystemWatcher fileSystemWatcher = mock(FileSystemWatcher.class);
        FileDetector fileDetector = mock(FileDetector.class);
        LogFileProcessor logFileProcessor = mock(LogFileProcessor.class);

        when(fileSystemWatcher.pollEvents()).thenReturn(List.of());

        DirectoryWatcher directoryWatcher = new DirectoryWatcher(
                fileSystemWatcher,
                fileDetector,
                logFileProcessor
        );

        //when
        directoryWatcher.watch();

        //then
        verifyNoInteractions(logFileProcessor);
    }
}