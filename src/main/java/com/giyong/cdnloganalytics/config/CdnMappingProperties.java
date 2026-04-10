package com.giyong.cdnloganalytics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "cdn.mapping")
public class CdnMappingProperties {
    private Map<String, String> mappings = new HashMap<>();
}
