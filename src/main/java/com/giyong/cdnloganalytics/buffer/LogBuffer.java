package com.giyong.cdnloganalytics.buffer;

import com.giyong.cdnloganalytics.config.LogBufferProperties;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogBuffer {
    private final List<ParsedLog> buffer = new ArrayList<>();
    private final int capacity;

    public LogBuffer(LogBufferProperties logBufferProperties) {
        capacity = logBufferProperties.getSize();
    }

    public boolean add(ParsedLog parsedLog) {
        buffer.add(parsedLog);
        return buffer.size() == capacity;
    }

    public int getSize() {
        return buffer.size();
    }

    public List<ParsedLog> flush() {
        List<ParsedLog> parsedLogs = new ArrayList<>(buffer);
        buffer.clear();
        return parsedLogs;
    }
}
