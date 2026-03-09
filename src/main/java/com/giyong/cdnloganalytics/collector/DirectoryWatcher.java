package com.giyong.cdnloganalytics.collector;

import com.giyong.cdnloganalytics.processor.LogFileProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.*;

@Component
@RequiredArgsConstructor
public class DirectoryWatcher {

    private final LogFileProcessor logFileProcessor;

    public void watch() throws Exception {

        Path path = Paths.get("./logs");

        WatchService watchService =
                FileSystems.getDefault().newWatchService();

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
        );

        System.out.println("Watching logs directory...");

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                Path fileName = (Path) event.context();
                Path fullPath = path.resolve(fileName);
                System.out.println("New log file detected: " + fullPath);

                logFileProcessor.process(fullPath);
            }

            key.reset();
        }
    }
}
