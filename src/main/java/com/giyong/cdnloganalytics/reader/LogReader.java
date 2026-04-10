package com.giyong.cdnloganalytics.reader;

import com.giyong.cdnloganalytics.common.CdnProvider;
import com.giyong.cdnloganalytics.service.LogIngestService;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//@Component
@RequiredArgsConstructor
public class LogReader {
    private final LogIngestService logIngestService;
    private final Path path;
    private final CdnProvider cdnProvider;

    public void read() {
        //1. 파일을 한줄씩 읽기
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //2. LogIngestService.ingest 호출
                logIngestService.ingest(line, cdnProvider);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}