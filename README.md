# CDN 로그 분석 시스템

AWS CloudFront 로그를 수집·파싱하여 채널/프로그램별 통계를 집계하고, 권한 기반으로 조회할 수 있는 시스템입니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| 언어 | Kotlin (JVM 21) |
| 프레임워크 | Spring Boot 3.4, Spring Batch, Spring Security |
| 빌드 | Gradle Kotlin DSL (멀티 모듈) |
| DB | MySQL 8.4, Redis 7.4 |
| 인증 | JWT (AccessToken + RefreshToken) |
| 마이그레이션 | Flyway |
| 테스트 | JUnit5, MockK |
| 프론트엔드 | Next.js |

## 프로젝트 구조

```
cdn-log-analytics/
├── docker-compose.yml           # MySQL + Redis 로컬 환경
├── frontend/                    # Next.js 프론트엔드
└── backend/                     # Spring Boot 백엔드 (Gradle 멀티 모듈)
    ├── build.gradle.kts         # 루트 공통 설정
    ├── settings.gradle.kts      # 모듈 등록
    ├── core/                    # 공통 도메인, 유틸
    ├── api/                     # REST API 서버 (port: 8080)
    │   └── resources/
    │       └── db/migration/    # Flyway SQL 마이그레이션
    └── batch/                   # Spring Batch 서버 (port: 8081)
```

## 로컬 실행 방법

### 1. 인프라 실행

```bash
docker-compose up -d
```

### 2. 백엔드 실행

```bash
cd backend

# API 서버
./gradlew :api:bootRun

# Batch 서버
./gradlew :batch:bootRun
```

## Mock 데이터에 대하여

실제 CDN 환경(S3 + CloudFront)이 없으므로, CloudFront 로그 포맷 기반의 Mock 데이터 생성기를 직접 구현하여 대체합니다.
포트폴리오 특성상 비용 절감을 위한 결정이며, 실제 로그 포맷과 동일한 구조로 데이터를 생성합니다.

## 권한 구조

| 역할 | 조회 가능 범위 |
|---|---|
| ADMIN | 모든 채널 통계 |
| USER | 담당 채널 통계만 (`user_channels` 테이블 기준) |
