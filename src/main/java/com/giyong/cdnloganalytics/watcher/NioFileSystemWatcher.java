package com.giyong.cdnloganalytics.watcher;

import com.giyong.cdnloganalytics.config.LogProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Component
public class NioFileSystemWatcher implements FileSystemWatcher{
    private final Path path;

    public NioFileSystemWatcher(LogProperties logProperties) {
        this.path = Paths.get(logProperties.getDirectory());
    }

    @Override
    public List<String> pollEvents() {
        try (Stream<Path> stream = Files.walk(path)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(p -> p.toAbsolutePath().toString())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}