package com.giyong.cdnloganalytics.watcher;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.processor.LogFileProcessor;
import com.giyong.cdnloganalytics.reader.LogReader;
import com.giyong.cdnloganalytics.service.LogIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.*;

@Component
@RequiredArgsConstructor
public class DirectoryWatcher {

    private final LogReader logReader;

    public void watch() throws Exception {

        Path logRoot = Paths.get("./logs");

        WatchService watchService =
                FileSystems.getDefault().newWatchService();

        DirectoryStream<Path> stream = Files.newDirectoryStream(logRoot);
        for (Path path : stream) {
            if (Files.isDirectory(path)) {
                path.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE
                );
                System.out.println("Watching logs directory..." + path);
            }
        }

        while (true) {
            WatchKey key = watchService.take();
            Path dir = (Path) key.watchable();

            for (WatchEvent<?> event : key.pollEvents()) {
                Path fileName = (Path) event.context();
                Path fullPath = dir.resolve(fileName);

                CdnProvider cdnProvider = detectProvider(dir);

                System.out.println(
                        "New log detected: "
                                + fullPath
                                + " cdnProvider=" + cdnProvider
                );

                logReader.read(fullPath, cdnProvider);
            }

            key.reset();
        }
    }

    private CdnProvider detectProvider(Path dir) {

        String name = dir.getFileName().toString().toLowerCase();

        return switch (name) {
            case "aws" -> CdnProvider.AWS;
            case "akamai" -> CdnProvider.AKAMAI;
            case "tencent" -> CdnProvider.TENCENT;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };

    }
}
