package com.giyong.cdnloganalytics.service;

import com.giyong.cdnloganalytics.buffer.LogBuffer;
import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import com.giyong.cdnloganalytics.entity.RawLog;
import com.giyong.cdnloganalytics.mapper.LogMapper;
import com.giyong.cdnloganalytics.parser.LogParser;
import com.giyong.cdnloganalytics.parser.LogParserFactory;
import com.giyong.cdnloganalytics.repository.RawLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogIngestServiceTest {
    @Mock
    private LogParserFactory logParserFactory;

    @Mock
    private LogBuffer logBuffer;

    @Mock
    private LogMapper logMapper;

    @Mock
    private RawLogRepository rawLogRepository;

    @InjectMocks
    private LogIngestService logIngestService;

    @Test
    void should_get_log_parser_from_log_parser_factory() {
        //given
        LogParser logParser = mock(LogParser.class);
        when(logParserFactory.getParser(CdnProvider.AWS)).thenReturn(logParser);

        //when
        logIngestService.ingest("log", CdnProvider.AWS);

        //then
        verify(logParserFactory).getParser(CdnProvider.AWS);
    }

    @Test
    void should_parse_log_line() {
        //given
        LogParser logParser = mock(LogParser.class);
        when(logParserFactory.getParser(CdnProvider.AWS)).thenReturn(logParser);

        String logLine = "log";
        ParsedLog parsedLog = mock(ParsedLog.class);
        when(logParser.parse(logLine)).thenReturn(parsedLog);

        //when
        logIngestService.ingest(logLine, CdnProvider.AWS);

        //then
        verify(logParser).parse(logLine);
    }

    @Test
    void should_add_parsed_log_to_buffer() {
        //given
        LogParser logParser = mock(LogParser.class);
        when(logParserFactory.getParser(CdnProvider.AWS)).thenReturn(logParser);
        ParsedLog parsedLog = mock(ParsedLog.class);
        String logLine = "log";
        when(logParser.parse(logLine)).thenReturn(parsedLog);
        when(logBuffer.add(parsedLog)).thenReturn(false);

        //when
        logIngestService.ingest(logLine, CdnProvider.AWS);

        //then
        verify(logBuffer).add(parsedLog);
    }

    @Test
    void should_convert_buffer_to_raw_log_entity_list() {
        //given
        LogParser logParser = mock(LogParser.class);
        ParsedLog parsedLog = mock(ParsedLog.class);
        String logLine = "log";
        RawLog rawLog = mock(RawLog.class);

        when(logParserFactory.getParser(CdnProvider.AWS)).thenReturn(logParser);
        when(logParser.parse(logLine)).thenReturn(parsedLog);
        when(logBuffer.add(parsedLog)).thenReturn(true);
        when(logBuffer.flush()).thenReturn(List.of(parsedLog));
        when(logMapper.toEntities(anyList())).thenReturn(List.of(rawLog));

        //when
        logIngestService.ingest(logLine, CdnProvider.AWS);

        //then
        verify(logMapper).toEntities(List.of(parsedLog));
    }

    @Test
    void should_save_all_raw_logs() {
        //given
        LogParser logParser = mock(LogParser.class);
        ParsedLog parsedLog = mock(ParsedLog.class);
        String logLine = "log";
        RawLog rawLog = mock(RawLog.class);

        when(logParserFactory.getParser(CdnProvider.AWS)).thenReturn(logParser);
        when(logParser.parse(logLine)).thenReturn(parsedLog);
        when(logBuffer.add(parsedLog)).thenReturn(true);
        when(logBuffer.flush()).thenReturn(List.of(parsedLog));
        when(logMapper.toEntities(anyList())).thenReturn(List.of(rawLog));

        //when
        logIngestService.ingest(logLine, CdnProvider.AWS);

        //then
        verify(rawLogRepository).saveAll(argThat((List<RawLog> list) ->
                    list.size() == 1 && list.contains(rawLog)
                ));
    }
}