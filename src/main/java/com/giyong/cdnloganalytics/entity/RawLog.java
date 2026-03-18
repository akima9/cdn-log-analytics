package com.giyong.cdnloganalytics.entity;

import com.giyong.cdnloganalytics.common.CdnProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class RawLog {
    @Id
    @GeneratedValue
    private Long id;
    private CdnProvider cdnProvider;
    private Date requestTime;
    private Long channelId;
    private Long programId;
    private String ip;
    private int status;
    private Long bytes;
    private String edgeLocation;
}