package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;

public interface LogParser {
    ParsedLog parse(String line, CdnProvider cdnProvider);
}
