package com.giyong.cdnloganalytics.service;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.parser.LogParser;
import com.giyong.cdnloganalytics.parser.LogParserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogIngestService {
    private final LogParserFactory logParserFactory;

    public void process(String line, CdnProvider cdnProvider) {
        System.out.println("line = " + line);
        System.out.println("cdnProvider = " + cdnProvider);
        //1. 로그 파싱 후 DTO에 담기
        LogParser logParser = logParserFactory.getParser(cdnProvider);
        System.out.println("logParser = " + logParser);
//        ParsedLog parsedLog = logParser.parse(line);
//        System.out.println("parsedLog = " + parsedLog);
        //2. DTO를 버퍼에 모으기
        //3. 버퍼에 모은 데이터 DB에 저장하기
    }
}
