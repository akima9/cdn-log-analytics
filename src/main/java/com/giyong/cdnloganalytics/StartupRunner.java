package com.giyong.cdnloganalytics;

import com.giyong.cdnloganalytics.watcher.DirectoryWatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
    private final DirectoryWatcher directoryWatcher;

    @Override
    public void run(String... args) throws Exception {
        directoryWatcher.watch();
    }
}
