package com.giyong.cdnloganalytics.watcher;

import com.giyong.cdnloganalytics.config.LogProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NioFileSystemWatcher implements FileSystemWatcher{
    private final Path path;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keyMap = new HashMap<>();

    public NioFileSystemWatcher(LogProperties logProperties) {
        try {
            this.path = Paths.get(logProperties.getDirectory());
            this.watchService = FileSystems.getDefault().newWatchService();

            registerAll(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerAll(Path start) throws IOException {
        Files.walk(start)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                        try {
                            WatchKey key = dir.register(
                                    watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE
                            );
                            keyMap.put(key, dir);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
    }

    @Override
    public List<String> pollEvents() {
        List<String> result = new ArrayList<>();

        try {
            // 이벤트 올 때까지 대기
            WatchKey key = watchService.take();
            Path dir = keyMap.get(key);

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path fileName = (Path) event.context();
                    Path fullPath = dir.resolve(fileName);

                    result.add(fullPath.toAbsolutePath().toString());
                }
            }

            key.reset();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return result;
    }
}