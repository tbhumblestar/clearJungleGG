# 9_DATA_STRATEGY.md (데이터 전략서)

> 백엔드에서 데이터를 어떻게 수집하고, 정제하고, 저장하는지를 정의하는 문서입니다.

---

## [DS-1] 수집 범위 및 배치 주기

### 1-1. 개요
천상계 유저들의 솔로 랭크 경기를 수집하여 다음 데이터를 구축한다.

| 데이터 | 설명 | 저장 위치 |
|--------|------|-----------|
| 정글 매치 원천 기록 | 모든 정글러의 경기별 기록 (SSOT) | `match_record` |
| 챔피언 통계 | 승률, 픽률, 밴률 | `champion_stats` |
| 챔피언 상성 | 정글 챔피언 간 1:1 승률 (카운터 밴 추천용) | `champion_matchup` |
| 풀캠프 클리어 기록 | 순수성 검증 통과한 클리어 타임 (영상 녹화 후보) | `clear_record` |

### 1-2. 수집 대상
* **서버:** 한국(KR)
* **티어:** 그랜드마스터 + 챌린저 (약 1,000명)
  * 마스터 티어 확장은 Rate Limit 검증 후 결정 *(→ `10_BACKLOG.md` BACKLOG-005)*
* **큐 타입:** 솔로 랭크 (queue=420)
* **포지션:** 정글(JUNGLE) — 매치당 2명

### 1-3. 배치 주기
* **매일 1회** 실행 (Spring Scheduler)
* 전날 00:00~23:59(KST) 매치를 대상으로 수집
* 신기록 발견 시 실시간 알림은 추후 구현 *(→ `10_BACKLOG.md` BACKLOG-007)*

### 1-4. 파이프라인 개요

```
1. 유저 풀 확보
   API-D (챌린저) + API-E (그랜드마스터) → puuid ~1,000명 (인메모리)

2. 매치 ID 수집
   API-F (by puuid, queue=420, 전날 범위) → match_id 목록
   ※ 중복 제거: match_record에 이미 있는 match_id는 건너뜀

3. 매치 상세 처리 → [DS-3]
   API-G (매치 상세) → 정글러 추출, 메타데이터 파싱 → match_record 저장

4. 타임라인 분석 → [DS-4]
   API-H (타임라인) → 시작 위치, 순수성 검증, 클리어 타임 → clear_record 저장

5. 통계 집계 → [DS-5]
   match_record 기반 → champion_stats, champion_matchup 갱신
```

---

## [DS-2] DB 테이블 설계

### ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    champion ||--o{ match_record : "champion_id"
    champion ||--o{ match_record : "opponent_champion_id"
    champion ||--o{ champion_stats : "champion_id"
    champion ||--o{ champion_matchup : "champion_id"
    champion ||--o{ champion_matchup : "opponent_champion_id"
    match_record ||--o| clear_record : "match_record_id"

    champion {
        VARCHAR id PK "영문 ID (LeeSin)"
        INT key "숫자 식별자 (64)"
        VARCHAR name "한글 이름 (리 신)"
        VARCHAR title "이명 (눈먼 수도승)"
        VARCHAR patch_version "동기화 패치"
        JSONB champion_history "변경 이력"
    }

    match_record {
        BIGSERIAL id PK "Auto increment"
        VARCHAR match_id UK "Riot 매치 ID"
        VARCHAR champion_id FK "플레이 챔피언"
        VARCHAR team "BLUE / RED"
        BOOLEAN win "승리 여부"
        VARCHAR opponent_champion_id FK "상대 정글 챔피언"
        JSONB runes "룬 세팅"
        VARCHAR summoner_spells "소환사 주문"
        VARCHAR start_position "시작 캠프"
        JSONB banned_champions "밴 목록 (최대 10)"
        VARCHAR patch_version "패치 버전"
        VARCHAR summoner_name "Riot ID 이름"
        VARCHAR summoner_tag "Riot ID 태그"
        VARCHAR summoner_tier "수집 시점 티어"
        TIMESTAMP game_started_at "게임 시작 시각"
    }

    champion_stats {
        VARCHAR champion_id PK_FK "챔피언 ID"
        VARCHAR patch_version PK "패치 버전"
        INT pick_count "픽 수"
        INT win_count "승리 수"
        INT ban_count "밴 수"
        INT total_matches "전체 매치 수"
    }

    champion_matchup {
        VARCHAR champion_id PK_FK "기준 챔피언"
        VARCHAR opponent_champion_id PK_FK "상대 챔피언"
        VARCHAR patch_version PK "패치 버전"
        INT wins "승리 횟수"
        INT losses "패배 횟수"
    }

    clear_record {
        BIGSERIAL id PK "Auto increment"
        BIGINT match_record_id FK_UK "원천 매치 기록 (1:1)"
        INT clear_time_ms "4레벨 달성 시간 (ms)"
        VARCHAR youtube_video_id "영상 ID (nullable)"
    }
```

### 2-1. champion (챔피언 메타데이터)

Data Dragon에서 패치당 1회 동기화. 챔피언 기본 정보 + 변경 이력 관리.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | VARCHAR (PK) | 영문 ID (예: `LeeSin`) |
| `key` | INT | 숫자 식별자 (예: `64`) |
| `name` | VARCHAR | 한글 이름 (예: `리 신`) |
| `title` | VARCHAR | 이명 (예: `눈먼 수도승`) |
| `patch_version` | VARCHAR | 마지막 동기화 패치 버전 |
| `champion_history` | JSONB | 변경 이력 (아래 구조 참고) |
| `updated_at` | TIMESTAMP | 마지막 동기화 시각 |

**champion_history 구조:**
```json
[
  {
    "patch": "16.7",
    "changed_at": "2026-04-15T00:00:00",
    "changes": {
      "title": { "before": "눈먼 수도승", "after": "맹인 수도승" }
    }
  }
]
```
* 패치 동기화 시 기존 값과 비교하여 변경점이 있으면 `champion_history` 배열에 추가
* 변경이 없으면 `patch_version`과 `updated_at`만 갱신

### 2-2. match_record (원천 데이터 — SSOT)

모든 정글 매치 기록의 단일 진실 공급원. 매치당 정글러 2명 → **2행** 저장.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT (PK) | Auto increment |
| `match_id` | VARCHAR | Riot 매치 ID (예: `KR_8151787115`) |
| `champion_id` | VARCHAR | 챔피언 영문 ID (예: `LeeSin`) |
| `team` | VARCHAR | `BLUE` / `RED` (teamId 100=BLUE, 200=RED) |
| `win` | BOOLEAN | 팀 승리 여부 |
| `opponent_champion_id` | VARCHAR | 상대 정글러 챔피언 ID |
| `runes` | JSONB | 룬 정보 (주 룬 트리 + 부 룬 트리 + 스탯 파편) |
| `summoner_spells` | VARCHAR | 소환사 주문 2개 (예: `"11,4"` = 강타+점멸) |
| `start_position` | VARCHAR | 1분 시점 시작 캠프 (타임라인 분석 후 채움, nullable) |
| `banned_champions` | JSONB | 해당 매치의 밴 목록 (챔피언 ID 배열, 최대 10개) |
| `patch_version` | VARCHAR | 패치 버전 (예: `16.6`) |
| `summoner_name` | VARCHAR | Riot ID 이름 (riotIdGameName) |
| `summoner_tag` | VARCHAR | Riot ID 태그 (riotIdTagline) |
| `summoner_tier` | VARCHAR | 수집 시점 티어 (`CHALLENGER` / `GRANDMASTER`) |
| `game_started_at` | TIMESTAMP | 게임 시작 시각 |
| `created_at` | TIMESTAMP | 레코드 생성 시각 |

**인덱스:**
* `UNIQUE(match_id, champion_id)` — 동일 매치+챔피언 중복 방지
* `INDEX(champion_id)` — 챔피언별 조회
* `INDEX(champion_id, opponent_champion_id)` — 상성 조회

### 2-3. champion_stats (파생 — 통계 집계)

`match_record` 기반으로 배치에서 집계. 챔피언별·패치별 통계.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `champion_id` | VARCHAR (PK) | 챔피언 ID |
| `patch_version` | VARCHAR (PK) | 패치 버전 |
| `pick_count` | INT | 정글로 픽된 횟수 |
| `win_count` | INT | 승리 횟수 |
| `ban_count` | INT | 밴된 횟수 |
| `total_matches` | INT | 해당 패치 전체 분석 매치 수 (밴률 분모) |
| `updated_at` | TIMESTAMP | 마지막 집계 시각 |

**산출 공식:**
* 승률 = `win_count / pick_count`
* 픽률 = `pick_count / total_matches`
* 밴률 = `ban_count / total_matches`

### 2-4. champion_matchup (파생 — 상성)

정글 챔피언 간 1:1 승률. 카운터 밴 추천의 기반 데이터.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `champion_id` | VARCHAR (PK) | 기준 챔피언 |
| `opponent_champion_id` | VARCHAR (PK) | 상대 챔피언 |
| `patch_version` | VARCHAR (PK) | 패치 버전 |
| `wins` | INT | 기준 챔피언 승리 횟수 |
| `losses` | INT | 기준 챔피언 패배 횟수 |
| `updated_at` | TIMESTAMP | 마지막 집계 시각 |

**예시:** 리신 vs 그레이브즈 = wins: 45, losses: 55 → 리신 입장 승률 45%

### 2-5. clear_record (파생 — 가치있는 경기)

순수성 검증(DS-4)을 통과한 풀캠프 클리어 기록. 영상 녹화 후보이자 리더보드 데이터.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT (PK) | Auto increment |
| `match_record_id` | BIGINT (FK) | match_record 참조 |
| `clear_time_ms` | INT | 4레벨 달성 시간 (밀리초, 게임 내 시간) |
| `youtube_video_id` | VARCHAR | YouTube 영상 ID (Phase 3에서 채움, nullable) |
| `created_at` | TIMESTAMP | 레코드 생성 시각 |

**조회 패턴:**
* 챔피언별 리더보드: `JOIN match_record WHERE champion_id = ? ORDER BY clear_time_ms LIMIT 10`
* 챔피언별 최고 기록: `MIN(clear_time_ms) WHERE champion_id = ?`

---

## [DS-3] 매치 상세 처리 (API-G 파싱)

### 3-1. 정글러 추출
* `info.participants[]`에서 `teamPosition === "JUNGLE"`인 참가자 2명 추출
* 각 정글러에 대해 `match_record` 1행씩 생성

### 3-2. 수집 필드 매핑

| match_record 컬럼 | API-G 필드 |
|-------------------|-----------|
| `match_id` | `metadata.matchId` |
| `champion_id` | `participants[].championName` |
| `team` | `participants[].teamId` (100→BLUE, 200→RED) |
| `win` | `participants[].win` |
| `opponent_champion_id` | 상대 팀 정글러의 `championName` |
| `runes` | `participants[].perks` |
| `summoner_spells` | `participants[].summoner1Id`, `summoner2Id` |
| `patch_version` | `info.gameVersion` → 앞 2자리 (예: `"16.6.756.9613"` → `"16.6"`) |
| `summoner_name` | `participants[].riotIdGameName` |
| `summoner_tag` | `participants[].riotIdTagline` |
| `game_started_at` | `info.gameStartTimestamp` (Epoch ms → Timestamp) |

### 3-3. 밴 데이터 수집
* `info.teams[].bans[]`에서 밴된 챔피언 ID 목록 추출
* 양 팀 합산 최대 10개 → `banned_champions` JSONB 배열로 저장
* 매치당 동일한 밴 목록이 2행(정글러 2명)에 모두 들어감 (비정규화, SSOT 우선)

### 3-4. 리메이크 필터링
* `info.gameDuration`이 극단적으로 짧은 경기(5분 미만)는 리메이크로 판단하여 건너뜀

---

## [DS-4] 타임라인 분석 (API-H)

### 4-1. 시작 위치 판별
* 게임 시간 **1분 시점**의 유저 좌표(`position`)를 확인하여 시작 캠프(레드/블루/칼날부리)를 기록한다.
* `frames[1].participantFrames[participantId].position`에서 좌표 추출
* 판별 결과를 `match_record.start_position`에 업데이트

### 4-2. 순수성 검증 (Purity Check)
4레벨 달성 전까지 외부 변수(챔피언 간 교전, 라인 미니언 등)가 개입했는지 검증한다. **하나라도 걸리면 clear_record에 저장하지 않는다.** 놓치는 데이터는 있어도, 오염된 데이터가 통과해서는 안 된다.

| # | 검증 조건 | 데이터 소스 | 판별 기준 |
|---|----------|-----------|----------|
| 1 | 킬/데스/어시스트 관여 | `CHAMPION_KILL` 이벤트 | 4레벨 달성 timestamp 이전의 모든 CHAMPION_KILL에서 `killerId`, `victimId`, `assistingParticipantIds`에 본인 participantId가 포함되면 실패 |
| 2 | 라인 미니언 경험치 흡수 | `participantFrames` 스냅샷 | 4레벨 달성 직전 프레임에서 `minionsKilled > 0`이면 실패 |
| 3 | 챔피언 대상 데미지 발생 | `participantFrames` 스냅샷 | 4레벨 달성 직전 프레임에서 `totalDamageDoneToChampions > 0`이면 실패 |

> **1번은 이벤트 기반**이라 타임스탬프가 정확하다. **2·3번은 1분 간격 스냅샷**이라 최대 1분의 오차가 있지만, 보수적 방향(오탐으로 폐기할지언정 오염 데이터 통과 불가)이므로 허용한다.

> **못 잡는 케이스:** 챔피언에게 일방적으로 데미지를 받기만 하고 도망친 경우 (받은 피해 중 챔피언 출처를 분리하는 필드가 API에 없음). 이 경우도 클리어 타임 손실로 자연 도태된다.

### 4-3. 4레벨 달성 시간 추출
* 타임라인의 `LEVEL_UP` 이벤트에서 4레벨 도달 시점의 **게임 내 실제 시간(timestamp)**을 그대로 추출한다.
* 예: 게임 시계 기준 3:05에 달성 → `clear_time_ms = 185000`
* 순수성 검증 통과 시 `clear_record`에 저장

### 4-4. 자연 도태 (Natural Filter-out)
아래 엣지 케이스는 별도 예외 처리 없이, "최고 기록만 의미 있다"는 비즈니스 특성으로 자연 필터링된다.

| 엣지 케이스 | 왜 자연 도태되는가 |
|------------|-------------------|
| 페이크 동선 (인베이드 방어 등) | 이동 시간 낭비 → 클리어 타임 지연 → 상위 기록 불가 |
| 미니언 경험치 흡수 (Leeching) | 라인 이동 시간 낭비 → 클리어 타임 지연 → 상위 기록 불가 |

---

## [DS-5] 통계 집계

### 5-1. champion_stats 집계

배치 완료 후 `match_record` 기반으로 `champion_stats`를 갱신한다.

```
픽 수 (pick_count):
  SELECT champion_id, COUNT(*) FROM match_record
  WHERE patch_version = ? GROUP BY champion_id

승리 수 (win_count):
  SELECT champion_id, COUNT(*) FROM match_record
  WHERE patch_version = ? AND win = true GROUP BY champion_id

밴 수 (ban_count):
  match_record.banned_champions JSONB 배열을 풀어서 챔피언별 등장 횟수 집계
  ※ 같은 매치의 2행에 동일 밴 목록이 있으므로, match_id 기준 중복 제거 필요

전체 매치 수 (total_matches):
  SELECT COUNT(DISTINCT match_id) FROM match_record WHERE patch_version = ?
```

### 5-2. champion_matchup 집계

```
SELECT champion_id, opponent_champion_id,
       SUM(CASE WHEN win THEN 1 ELSE 0 END) AS wins,
       SUM(CASE WHEN NOT win THEN 1 ELSE 0 END) AS losses
FROM match_record
WHERE patch_version = ?
GROUP BY champion_id, opponent_champion_id
```

* 양방향 자동 생성: 리신 vs 그레이브즈 행과 그레이브즈 vs 리신 행이 각각 존재 (match_record가 매치당 2행이므로)

---

## [DS-6] 메타데이터 관리

### 6-1. 챔피언 메타데이터 동기화
* **저장:** `champion` 테이블 (DS-2 섹션 2-1)
* **동기화 주기:** 패치당 1회. 새 패치 감지는 `versions.json`의 첫 번째 값 변경 여부로 판단.
* **동기화 흐름:**
  1. Data Dragon `champion.json` 조회
  2. DB의 기존 데이터와 비교
  3. 변경점 발견 시 → `champion_history`에 변경 이력 추가 + 컬럼 값 갱신
  4. 변경 없으면 → `patch_version`, `updated_at`만 갱신
  5. 신규 챔피언 발견 시 → INSERT

### 6-2. 룬/소환사 주문 메타데이터
* 패치당 1회 조회 후 **인메모리 캐싱** (DB 저장 불필요)
* 숫자 ID → 한글 이름 매핑용 (match_record의 runes/summoner_spells 해석)

### 6-3. 이미지 URL
* URL 패턴만 코드에서 조립. 실제 이미지는 Riot CDN에서 직접 서빙. 백엔드가 프록시할 필요 없음.
* 상세 URL 패턴 및 API 정보는 `6_RIOT_API_GUIDE.md` API-A ~ API-C3 참고
