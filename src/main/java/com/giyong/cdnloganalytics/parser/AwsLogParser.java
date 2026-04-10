package com.giyong.cdnloganalytics.parser;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.dto.ParsedLog;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component("AWS")
public class AwsLogParser implements LogParser {
    private final Map<String, Integer> fieldIndex = new HashMap<>();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneOffset.UTC);

    private Instant parseDateTime(String date, String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(date + " " + time, FORMATTER);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public ParsedLog parse(String line) {
        if (line.startsWith("#Version:")) {
            return null;
        }

        if (line.startsWith("#Fields:")) {
            createFieldIndex(line);
            return null;
        }

        String[] parts = line.split("\t");

        try {

            String date = parts[fieldIndex.get("date")];
            String time = parts[fieldIndex.get("time")];
            String ip = parts[fieldIndex.get("c-ip")];
            Long bytes = Long.parseLong(parts[fieldIndex.get("sc-bytes")]);
            String edgeLocation = parts[fieldIndex.get("x-edge-location")];
            int status = Integer.parseInt(parts[fieldIndex.get("sc-status")]);
            String query = parts[fieldIndex.get("cs-uri-query")];

            long channelId = 0L;
            long programId = 0L;

            if (!query.equals("-")) {

                String[] params = query.split("&");

                for (String param : params) {

                    String[] kv = param.split("=");

                    if (kv.length != 2) continue;

                    if (kv[0].equals("channel_id")) {
                        channelId = Long.parseLong(kv[1]);
                    }

                    if (kv[0].equals("program_id")) {
                        programId = Long.parseLong(kv[1]);
                    }
                }
            }

            Instant requestTime = parseDateTime(date, time);

            return ParsedLog.builder()
                    .cdnProvider(getProvider())
                    .requestTime(requestTime)
                    .channelId(channelId)
                    .programId(programId)
                    .ip(ip)
                    .status(status)
                    .bytes(bytes)
                    .edgeLocation(edgeLocation)
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid log: " + line, e);
        }
    }

    private void createFieldIndex(String line) {
        String[] fields = line.replace("#Fields: ", "").split(" ");
        for (int i = 0; i < fields.length; i++) {
            fieldIndex.put(fields[i], i);
        }
    }

    @Override
    public CdnProvider getProvider() {
        return CdnProvider.AWS;
    }

}
