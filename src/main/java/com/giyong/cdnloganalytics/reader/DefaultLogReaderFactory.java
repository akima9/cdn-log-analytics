package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.service.LogIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class DefaultLogReaderFactory implements LogReaderFactory{
    private final LogIngestService logIngestService;

    @Override
    public LogReader create(Path path, CdnProvider cdnProvider) {
        return new LogReader(logIngestService, path, cdnProvider);
    }
}
