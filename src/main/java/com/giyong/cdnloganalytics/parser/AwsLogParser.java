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

//TODO: split()은 성능 문제가 있을 수 있음. history 참고.
@Component("AWS")
public class AwsLogParser implements LogParser {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneOffset.UTC);

    private Map<String, Integer> fieldIndex = new HashMap<>();

    private Instant parseDateTime(String date, String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(date + " " + time, FORMATTER);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public ParsedLog parse(String line, CdnProvider cdnProvider) {
        // Fields 라인 처리
        if (line.startsWith("#Fields:")) {
            parseFields(line);
            return null;
        }

        // 기타 메타라인 skip
        if (line.startsWith("#")) {
            return null;
        }

        if (fieldIndex.isEmpty()) {
            System.out.println("Fields not initialized yet.");
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

            Long channelId = 0L;
            Long programId = 0L;

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
                    .cdnProvider(cdnProvider)
                    .requestTime(requestTime)
                    .channelId(channelId)
                    .programId(programId)
                    .ip(ip)
                    .status(status)
                    .bytes(bytes)
                    .edgeLocation(edgeLocation)
                    .build();

        } catch (Exception e) {
            System.out.println("Failed to parse log line: " + line);
            return null;
        }
    }

    private void parseFields(String line) {

        fieldIndex.clear();

        String fieldsPart = line.replace("#Fields: ", "");
        String[] fields = fieldsPart.split(" ");

        for (int i = 0; i < fields.length; i++) {
            fieldIndex.put(fields[i], i);
        }

        System.out.println("Fields initialized: " + fieldIndex);
    }
}
