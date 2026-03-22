package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogParserFactoryTest {
    @Test
    void should_return_aws_log_parser() {
        //given
        AwsLogParser awsLogParser = new AwsLogParser();
        Map<String, LogParser> parserMap = Map.of(CdnProvider.AWS.name(), awsLogParser);

        LogParserFactory logParserFactory = new LogParserFactory(parserMap);

        //when
        LogParser logParser = logParserFactory.getParser(CdnProvider.AWS);

        //then
        assertEquals(awsLogParser, logParser);
    }

    @Test
    void should_throw_exception_when_parser_not_found() {
        LogParserFactory logParserFactory = new LogParserFactory(Map.of());

        assertThrows(IllegalArgumentException.class, () -> {
            logParserFactory.getParser(CdnProvider.AWS);
        });
    }

}