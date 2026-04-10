package com.giyong.cdnloganalytics.watcher;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileDetectorTest {

    //새로운 파일만 감지한다.
    @Test
    void should_detect_new_files() {
        //given
        FileDetector fileDetector = new FileDetector();

        //when
        List<String> result1 = fileDetector.detectNewFiles(List.of("a.log"));
        List<String> result2 = fileDetector.detectNewFiles(List.of("a.log", "b.log"));

        //then
        assertEquals(result1, List.of("a.log"));
        assertEquals(result2, List.of("b.log"));
    }

}