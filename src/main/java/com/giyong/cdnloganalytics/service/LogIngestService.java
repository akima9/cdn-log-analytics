package com.giyong.cdnloganalytics.service;

import com.giyong.cdnloganalytics.buffer.LogBuffer;
import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import com.giyong.cdnloganalytics.entity.RawLog;
import com.giyong.cdnloganalytics.mapper.LogMapper;
import com.giyong.cdnloganalytics.parser.LogParser;
import com.giyong.cdnloganalytics.parser.LogParserFactory;
import com.giyong.cdnloganalytics.repository.RawLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogIngestService {
    private final LogParserFactory logParserFactory;
    private final LogBuffer logBuffer;
    private final LogMapper logMapper;
    private final RawLogRepository rawLogRepository;

    public void ingest(String line, CdnProvider cdnProvider) {
        //1. parser 선택
        LogParser logParser = logParserFactory.getParser(cdnProvider);
        //2. parsing
        ParsedLog parsedLog = logParser.parse(line);
        //3. LogBuffer에 담기
        if (logBuffer.add(parsedLog)) {
            List<ParsedLog> parsedLogs = logBuffer.flush();
            List<RawLog> rawLogs = logMapper.toEntities(parsedLogs);
            rawLogRepository.saveAll(rawLogs);
        }
    }
}
