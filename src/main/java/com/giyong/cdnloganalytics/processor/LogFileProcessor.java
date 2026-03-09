package com.giyong.cdnloganalytics.processor;

import com.giyong.cdnloganalytics.parser.LogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LogFileProcessor {
    private final LogParser logParser;

    public void process(Path filePath) {

        System.out.println("Processing file: " + filePath);

        try (Stream<String> lines = Files.lines(filePath)) {

            lines.forEach(line -> logParser.parse(line));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
