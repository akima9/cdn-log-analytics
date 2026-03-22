package com.giyong.cdnloganalytics.entity;

import com.giyong.cdnloganalytics.common.CdnProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RawLogTest {
    @Test
    void should_create_raw_log() {
        Instant now = Instant.now();
        RawLog rawLog = RawLog.builder()
                .cdnProvider(CdnProvider.AWS)
                .requestTime(now)
                .channelId(1001L)
                .programId(55501L)
                .ip("203.0.113.10")
                .status(200)
                .bytes(512L)
                .edgeLocation("ICN1")
                .build();

        assertEquals(CdnProvider.AWS, rawLog.getCdnProvider());
        assertEquals(now, rawLog.getRequestTime());
        assertEquals(1001L, rawLog.getChannelId());
        assertEquals(55501L, rawLog.getProgramId());
        assertEquals("203.0.113.10", rawLog.getIp());
        assertEquals(200, rawLog.getStatus());
        assertEquals(512L, rawLog.getBytes());
        assertEquals("ICN1", rawLog.getEdgeLocation());
    }
}