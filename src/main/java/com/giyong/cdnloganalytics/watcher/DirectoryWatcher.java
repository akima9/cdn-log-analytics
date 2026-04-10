package com.giyong.cdnloganalytics.watcher;

import com.giyong.cdnloganalytics.service.LogFileProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DirectoryWatcher {
    private final FileSystemWatcher fileSystemWatcher;
    private final FileDetector fileDetector;
    private final LogFileProcessor logFileProcessor;

    public void watch() {
        List<String> currentFiles = fileSystemWatcher.pollEvents();
        List<String> newFiles = fileDetector.detectNewFiles(currentFiles);
        for (String file : newFiles) {
            logFileProcessor.accept(file);
        }
    }
}
