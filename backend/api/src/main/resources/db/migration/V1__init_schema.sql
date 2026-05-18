-- V1__init_schema.sql
-- CDN 로그 분석 시스템 초기 스키마

CREATE TABLE users
(
    id            BIGINT                NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255)          NOT NULL UNIQUE,
    password_hash VARCHAR(255)          NOT NULL,
    role          ENUM ('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    created_at    DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE channels
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL UNIQUE,
    code       VARCHAR(50)  NOT NULL UNIQUE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE programs
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    channel_id BIGINT       NOT NULL,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(50)  NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_programs_channel_code (channel_id, code),
    UNIQUE KEY uq_programs_channel_name (channel_id, name),
    CONSTRAINT fk_programs_channel FOREIGN KEY (channel_id) REFERENCES channels (id)
);

CREATE TABLE user_channels
(
    user_id    BIGINT   NOT NULL,
    channel_id BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, channel_id),
    CONSTRAINT fk_uc_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_uc_channel FOREIGN KEY (channel_id) REFERENCES channels (id)
);

CREATE TABLE cdn_logs
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    cdn_provider  VARCHAR(50)  NOT NULL,
    request_time  DATETIME     NOT NULL,
    channel_id    BIGINT       NOT NULL,
    program_id    BIGINT       NOT NULL,
    ip            VARCHAR(45)  NOT NULL,
    status        SMALLINT     NOT NULL,
    bytes         BIGINT       NOT NULL,
    edge_location VARCHAR(100) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_cdn_logs_channel_program_time (channel_id, program_id, request_time),
    CONSTRAINT fk_cdn_logs_channel FOREIGN KEY (channel_id) REFERENCES channels (id),
    CONSTRAINT fk_cdn_logs_program FOREIGN KEY (program_id) REFERENCES programs (id)
);

CREATE TABLE daily_stats
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    stat_date      DATE           NOT NULL,
    channel_id     BIGINT         NOT NULL,
    program_id     BIGINT         NOT NULL,
    total_requests BIGINT         NOT NULL DEFAULT 0,
    total_bytes    BIGINT         NOT NULL DEFAULT 0,
    error_count    BIGINT         NOT NULL DEFAULT 0,
    avg_bytes      DECIMAL(20, 2) NOT NULL DEFAULT 0,
    unique_ips     INT            NOT NULL DEFAULT 0,
    peak_hour      TINYINT                 NULL     DEFAULT NULL,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_daily_stats (stat_date, channel_id, program_id),
    CONSTRAINT fk_daily_stats_channel FOREIGN KEY (channel_id) REFERENCES channels (id),
    CONSTRAINT fk_daily_stats_program FOREIGN KEY (program_id) REFERENCES programs (id)
);

CREATE TABLE monthly_stats
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    stat_year      INT            NOT NULL,
    stat_month     INT            NOT NULL,
    channel_id     BIGINT         NOT NULL,
    program_id     BIGINT         NOT NULL,
    total_requests BIGINT         NOT NULL DEFAULT 0,
    total_bytes    BIGINT         NOT NULL DEFAULT 0,
    error_count    BIGINT         NOT NULL DEFAULT 0,
    avg_bytes      DECIMAL(20, 2) NOT NULL DEFAULT 0,
    unique_ips     INT            NOT NULL DEFAULT 0,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_monthly_stats (stat_year, stat_month, channel_id, program_id),
    CONSTRAINT fk_monthly_stats_channel FOREIGN KEY (channel_id) REFERENCES channels (id),
    CONSTRAINT fk_monthly_stats_program FOREIGN KEY (program_id) REFERENCES programs (id)
);
