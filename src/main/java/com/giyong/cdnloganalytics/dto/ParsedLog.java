package com.giyong.cdnloganalytics.dto;

import com.giyong.cdnloganalytics.common.CdnProvider;
import lombok.Builder;

import java.time.Instant;
import java.util.Date;

@Builder
public class ParsedLog {
    private CdnProvider cdnProvider;
    private Instant requestTime;
    private Long channelId;
    private Long programId;
    private String ip;
    private int status;
    private Long bytes;
    private String edgeLocation;
}