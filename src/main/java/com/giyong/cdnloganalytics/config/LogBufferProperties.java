package com.giyong.cdnloganalytics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "log.buffer")
public class LogBufferProperties {
    private int size;
}
