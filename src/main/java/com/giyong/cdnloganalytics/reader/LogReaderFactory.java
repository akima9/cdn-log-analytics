package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.service.LogIngestService;

import java.nio.file.Path;

public interface LogReaderFactory {
    LogReader create(Path path, CdnProvider cdnProvider);
}
