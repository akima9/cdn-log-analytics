package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;

import java.util.Map;

public interface LogParser {
    ParsedLog parse(String line);

    CdnProvider getProvider();
}
