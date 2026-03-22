package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogParserFactory {
    private final Map<String, LogParser> parserMap;

    public LogParser getParser(CdnProvider cdnProvider) {
        LogParser logParser = parserMap.get(cdnProvider.name());

        if (logParser == null) {
            throw new IllegalArgumentException("Unsupported provider: " + cdnProvider);
        }

        return logParser;
    }
}
