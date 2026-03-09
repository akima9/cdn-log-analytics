package com.giyong.cdnloganalytics.parser;

import org.springframework.stereotype.Component;

@Component
public class LogParser {
    public void parse(String line) {

        String[] parts = line.split(" ");

        if (parts.length < 6) {
            System.out.println("Invalid log line: " + line);
            return;
        }

        String date = parts[0];
        String time = parts[1];
        String ip = parts[2];
        String channel = parts[3];
        String program = parts[4];
        String device = parts[5];
        String country = parts[6];

        System.out.println(
                "Parsed Log -> "
                        + date + " "
                        + time + " "
                        + ip + " "
                        + channel + " "
                        + program + " "
                        + device + " "
                        + country
        );
    }
}
