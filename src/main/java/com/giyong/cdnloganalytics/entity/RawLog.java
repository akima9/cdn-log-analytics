package com.giyong.cdnloganalytics.entity;

import com.giyong.cdnloganalytics.common.CdnProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawLog {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private CdnProvider cdnProvider;

    private Instant requestTime;
    private Long channelId;
    private Long programId;
    private String ip;
    private int status;
    private Long bytes;
    private String edgeLocation;

    @Builder
    public RawLog(
            CdnProvider cdnProvider,
            Instant requestTime,
            Long channelId,
            Long programId,
            String ip,
            int status,
            Long bytes,
            String edgeLocation
    ) {
        this.cdnProvider = cdnProvider;
        this.requestTime = requestTime;
        this.channelId = channelId;
        this.programId = programId;
        this.ip = ip;
        this.status = status;
        this.bytes = bytes;
        this.edgeLocation = edgeLocation;
    }
}