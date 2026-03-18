package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.service.LogIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class LogReader {
    private final LogIngestService logIngestService;

    public void read(Path fullPath, CdnProvider cdnProvider) {
        //1. 파일을 한줄씩 읽기
        try (BufferedReader bufferedReader = Files.newBufferedReader(fullPath)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //2. LogIngestService.process 호출
                logIngestService.process(line, cdnProvider);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}