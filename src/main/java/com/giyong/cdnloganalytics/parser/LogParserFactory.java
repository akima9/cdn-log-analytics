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
        System.out.println("parserMap = " + parserMap);
        return parserMap.get(cdnProvider.name());
    }
}
