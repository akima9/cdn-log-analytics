package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
)
@ActiveProfiles("test")
class LogParserFactorySpringTest {
    @Autowired
    LogParserFactory logParserFactory;

    @Test
    void should_inject_aws_log_parser() {
        LogParser logParser = logParserFactory.getParser(CdnProvider.AWS);

        assertNotNull(logParser);
        assertInstanceOf(AwsLogParser.class, logParser);
    }
}