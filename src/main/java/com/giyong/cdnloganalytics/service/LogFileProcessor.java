package com.giyong.cdnloganalytics.service;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.domain.cdn.CdnProviderResolver;
import com.giyong.cdnloganalytics.reader.LogReader;
import com.giyong.cdnloganalytics.reader.LogReaderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class LogFileProcessor implements Consumer<String> {
    private final LogReaderFactory logReaderFactory;
    private final CdnProviderResolver cdnProviderResolver;

    @Override
    public void accept(String file) {
        Path path = Path.of(file);
        CdnProvider cdnProvider = cdnProviderResolver.resolve(path);
        LogReader logReader = logReaderFactory.create(path, cdnProvider);
        logReader.read();
    }
}
