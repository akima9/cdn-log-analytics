package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;

import java.nio.file.Path;

public interface LogReaderFactory {
    LogReader create(Path path, CdnProvider cdnProvider);
}
