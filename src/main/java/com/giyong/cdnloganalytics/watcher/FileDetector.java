package com.giyong.cdnloganalytics.watcher;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FileDetector {
    private final Set<String> knownFiles = new HashSet<>();

    public List<String> detectNewFiles(List<String> files) {
        ArrayList<String> newFiles = new ArrayList<>();

        for (String file : files) {
            if (!knownFiles.contains(file)) {
                knownFiles.add(file);
                newFiles.add(file);
            }
        }
        return newFiles;
    }
}
