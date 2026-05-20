# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

모든 Gradle 명령은 `backend/` 디렉터리에서 실행합니다.

```bash
# 인프라 (MySQL + Redis)
docker-compose up -d
docker-compose down

# 빌드
cd backend && ./gradlew build

# 모듈별 실행
cd backend && ./gradlew :api:bootRun       # port 8080
cd backend && ./gradlew :batch:bootRun     # port 8081

# 테스트 (전체)
cd backend && ./gradlew test

# 테스트 (단일 클래스)
cd backend && ./gradlew :api:test --tests "com.example.cdn.SomeServiceTest"

# 테스트 (단일 모듈)
cd backend && ./gradlew :api:test
cd backend && ./gradlew :batch:test

# 컴파일만
cd backend && ./gradlew :api:compileKotlin
```

## 아키텍처

### 모듈 구조

`backend/`는 Gradle 멀티 모듈 프로젝트입니다.

| 모듈 | 역할 | 주요 의존성 |
|---|---|---|
| `core` | 공통 도메인·유틸 | JPA, MySQL Driver |
| `api` | REST API 서버 | core, Web, Security, JWT, Redis, Validation, Flyway |
| `batch` | Spring Batch 서버 | core, Spring Batch |

- `api`와 `batch` 모두 `core`를 의존하며, `core`끼리는 의존하지 않습니다.
- 공통 패키지 루트: `com.example.cdn`
- JPA Entity와 Repository는 `core`에 정의합니다. `api`와 `batch`가 공유합니다.

### 두 개의 Spring Boot 앱

`api`와 `batch`는 별도의 Spring Boot 애플리케이션입니다. 스키마 마이그레이션(Flyway)은 `api`가 담당하며 `backend/api/src/main/resources/db/migration/`에 SQL 파일을 둡니다. 두 모듈 모두 `jpa.hibernate.ddl-auto: validate`이므로 스키마 변경은 반드시 Flyway 마이그레이션 파일로 작성해야 합니다.

Spring Batch의 자동 실행은 `spring.batch.job.enabled: false`로 비활성화되어 있습니다. Job은 `@EnableScheduling` + `@Scheduled`로 트리거됩니다.

### 인증 흐름

- **AccessToken**: 30분, stateless, 블랙리스트 없음 (만료까지 유효)
- **RefreshToken**: 14일, Redis에 `refresh_token:{userId}` 키로 저장, Rotation 적용 (재발급 시 기존 토큰 삭제 후 신규 발급)
- 로그아웃은 Redis에서 RefreshToken만 삭제합니다.

### 권한 분기

권한 처리는 서비스 레이어에서 수행합니다 (컨트롤러에서 하지 않음).

- `ADMIN`: `user_channels` 조회 없이 전체 채널 접근
- `USER`: `user_channels` 테이블 기준 담당 채널만 접근. `channelId` 미지정 시 권한 보유 채널 전체를 자동 필터

### 통계 집계 구조

- `cdn_logs`: Spring Batch가 파싱한 원본 로그 저장
- `daily_stats` / `monthly_stats`: Batch가 주기적으로 `cdn_logs`를 읽어 집계한 결과 저장
- 조회 API는 집계 테이블만 읽습니다 (`cdn_logs` 직접 조회 없음)
- `error_count` 기준: HTTP status `400` 이상

### DB / 인프라

| 항목 | 값 |
|---|---|
| DB | `cdn_log_analytics` |
| DB user | `cdn` / `cdn1234` |
| Redis | `localhost:6379` |
| API port | `8080` |
| Batch port | `8081` |

환경 변수 `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `JWT_SECRET`으로 기본값을 오버라이드할 수 있습니다.

### API 규칙

- Base URL: `/api/v1`
- 응답 래퍼: 성공 `{"data": ...}` / 실패 `{"error": {"code", "message", "details"}}`
- 페이지네이션: Spring Data `Pageable` 스타일 (`page`, `size`(최대 100), `sort`)
- 일별 통계 조회: 최대 90일, 월별: 최대 12개월 (DB 부하 방지)
- 채널 권한 변경: `PUT /admin/users/{userId}/channels` — `channelIds` 배열로 `user_channels`를 완전 대체

### Mock 데이터

실제 CloudFront + S3 환경 없이 CloudFront 로그 포맷 기반 Mock 데이터 생성기를 `batch` 모듈에 구현합니다. Mock 생성기는 비용 절감 목적의 의도된 설계입니다.

## 코딩 규칙

### TDD (필수)

이 프로젝트는 **엄격한 Red-Green-Refactor 사이클**로 개발합니다. 프로덕션 코드를 작성하기 전에 반드시 실패하는 테스트를 먼저 작성합니다.

**모든 기능 추가/변경 시 다음 순서를 지킵니다:**

1. **RED** — 실패하는 테스트를 먼저 작성
2. **RED 확인** — `./gradlew :<module>:test --tests "..."`를 실행하여 테스트가 의도한 이유로 실패하는지 확인 (컴파일 에러도 RED로 인정)
3. **GREEN** — 테스트를 통과시키는 **최소한의** 프로덕션 코드 작성. 다음 사이클에서 다룰 기능을 미리 구현하지 않습니다.
4. **GREEN 확인** — 테스트 재실행하여 통과 확인
5. **REFACTOR** — 테스트가 통과하는 상태를 유지하면서 중복 제거·구조 개선. 변경 후 다시 테스트 실행으로 GREEN 유지 확인

**중요한 원칙:**

- 한 사이클에서는 **하나의 동작(behavior)만** 다룹니다. 여러 케이스가 떠오르면 메모만 남기고 다음 사이클로 넘깁니다.
- 각 단계(RED/GREEN)에서 테스트 실행 결과를 사용자에게 보고합니다. "테스트를 통과시켰다"라고만 말하지 말고 실제 실행 출력을 보여줍니다.
- 사용자가 "구현해줘", "코드 추가해줘"라고만 요청해도 TDD 순서를 따릅니다. 사용자가 명시적으로 "테스트 없이"라고 말한 경우에만 건너뜁니다.
- 리팩터링 단계에서 테스트 코드도 함께 정리합니다 (중복 setup, 매직 넘버 등).

### 테스트 작성 규칙

- **테스트 메서드명**: 백틱(`` ` ``) + 한글 문장 형식
  ```kotlin
  @Test
  fun `로그인 시 잘못된 비밀번호면 INVALID_CREDENTIALS 예외를 던진다`() { ... }
  ```
- **프레임워크**: JUnit5 (`useJUnitPlatform()` 적용됨) + MockK 1.13.13
- **Spring 통합 테스트**: `@SpringBootTest`, `@DataJpaTest`, `@WebMvcTest` 등 슬라이스 테스트를 상황에 맞게 선택
- **테스트 위치**: 프로덕션 클래스와 동일한 패키지 구조로 `src/test/kotlin/` 하위에 배치

## 커밋 규칙

### 형식

[Conventional Commits](https://www.conventionalcommits.org/) 규칙을 따릅니다.

```
type(scope): 한글 제목 (50자 이내)

한글 본문 (선택)
- 변경 내용을 bullet으로 나열
```

### type

| type | 용도 |
|---|---|
| `feat` | 새 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변화 없는 코드 개선 |
| `test` | 테스트 추가·수정 |
| `docs` | 문서 변경 |
| `chore` | 빌드·설정·의존성 변경 |

### scope

모듈명 또는 도메인 영역을 소문자로 표기합니다.

| scope | 해당 영역 |
|---|---|
| `batch` | batch 모듈 |
| `api` | api 모듈 |
| `core` | core 모듈 |
| `infra` | DB 마이그레이션, 도커, CI 등 |

### 예시

```
feat(batch): 집계 Job 구현 (daily_stats, monthly_stats)

cdn_logs를 읽어 daily_stats, monthly_stats를 생성하는
StatsAggregationJob을 TDD로 구현.

- dailyStatsStep → monthlyStatsStep 순서로 실행
- 테스트 12개 추가
```

```
fix(api): 만료된 RefreshToken 재사용 시 500 대신 401 반환
```

```
refactor(core): DailyStats avgBytes 계산 로직을 확장 함수로 분리
```
