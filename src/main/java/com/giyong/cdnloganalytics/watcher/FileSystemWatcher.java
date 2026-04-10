package com.giyong.cdnloganalytics.watcher;

import java.util.List;

public interface FileSystemWatcher {
    List<String> pollEvents();
}
