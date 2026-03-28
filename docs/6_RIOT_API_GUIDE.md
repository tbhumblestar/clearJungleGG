# 5_RIOT_API_GUIDE.md (Riot Data Dragon API 사용 가이드)

## 1. 개요
Riot Games의 **Data Dragon (ddragon)** 은 챔피언 메타데이터와 이미지 리소스를 제공하는 정적 데이터 CDN입니다.
별도의 API Key 없이 사용 가능하며, 패치 버전별로 데이터가 관리됩니다.

---

## 2. API 엔드포인트

### [API-A] 버전 목록 조회
* **URL:** `https://ddragon.leagueoflegends.com/api/versions.json`
* **응답:** 버전 문자열 배열 (최신순 정렬). 배열의 첫 번째 값이 최신 버전.
* **예시 응답:** `["16.6.1", "16.5.1", "16.4.1", ...]`

### [API-B] 챔피언 전체 목록 조회 (한국어)
* **URL:** `https://ddragon.leagueoflegends.com/cdn/{version}/data/ko_KR/champion.json`
* **예시:** `https://ddragon.leagueoflegends.com/cdn/16.6.1/data/ko_KR/champion.json`
* **응답 구조:**
```json
{
  "type": "champion",
  "version": "16.6.1",
  "data": {
    "LeeSin": {
      "id": "LeeSin",
      "key": "64",
      "name": "리 신",
      "title": "눈먼 수도승",
      "tags": ["Fighter", "Assassin"],
      "image": { "full": "LeeSin.png" },
      "info": { "attack": 8, "defense": 5, "magic": 3, "difficulty": 6 },
      "stats": { ... }
    },
    ...
  }
}
```
* **주요 필드:**
  * `id` — 영문 고유 ID (URL 경로에 사용)
  * `name` — 한글 공식 이름 (띄어쓰기 포함, 예: "리 신")
  * `title` — 이명 (예: "눈먼 수도승")
  * `tags` — 역할 태그 (예: Fighter, Assassin, Mage, Tank 등)
* **총 챔피언 수:** 172개 (16.6.1 기준)

### [API-C] 챔피언 이미지 리소스

| 타입 | URL 패턴 | 비율 | 용도 |
|------|----------|------|------|
| **Portrait** (초상화) | `https://ddragon.leagueoflegends.com/cdn/{version}/img/champion/{championId}.png` | 1:1 (120x120) | 메인 페이지 챔피언 카드, 검색 자동완성 썸네일 |
| **Loading** (로딩 화면) | `https://ddragon.leagueoflegends.com/cdn/img/champion/loading/{championId}_0.jpg` | ~2:3 (세로) | 상세 페이지 좌측 패널 |
| **Splash** (스플래시 아트) | `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/{championId}_0.jpg` | ~16:9 (가로) | 상세 페이지 배경 |

> **참고:** Loading, Splash URL에는 버전이 포함되지 않음. `_0`은 기본 스킨을 의미.

### [API-C2] 룬 메타데이터 (한국어)
* **URL:** `https://ddragon.leagueoflegends.com/cdn/{version}/data/ko_KR/runesReforged.json`
* **용도:** Match Detail API의 `perks.styles[].selections[].perk` (숫자 ID) → 한글 룬 이름 매핑
* **응답 구조:** 룬 트리 배열. 각 트리 안에 `slots[].runes[]`로 개별 룬 정보 포함.
* **주요 룬 트리 ID:**

| style ID | 이름 |
|----------|------|
| `8000` | 정밀 |
| `8100` | 지배 |
| `8200` | 마법 |
| `8300` | 영감 |
| `8400` | 결의 |

### [API-C3] 소환사 주문 메타데이터 (한국어)
* **URL:** `https://ddragon.leagueoflegends.com/cdn/{version}/data/ko_KR/summoner.json`
* **용도:** Match Detail API의 `summoner1Id`/`summoner2Id` (숫자 ID) → 한글 주문 이름 매핑
* **주요 소환사 주문 ID:**

| key | 이름 |
|-----|------|
| `4` | 점멸 |
| `11` | 강타 |
| `6` | 유체화 |
| `14` | 점화 |
| `12` | 순간이동 |

> **캐싱:** 룬/소환사 주문 데이터는 챔피언 메타데이터와 동일하게 패치당 1회 조회 후 캐싱.

---

## 3. League API — 상위 티어 유저 목록 조회

> Phase 2 데이터 수집 파이프라인의 첫 단계: 수집 대상 유저 풀 확보

### [API-D] 챌린저 리그 조회
* **URL:** `https://kr.api.riotgames.com/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5`
* **인증:** `?api_key={RIOT_API_KEY}` 또는 `X-Riot-Token` 헤더
* **응답 구조:**
```json
{
  "tier": "CHALLENGER",
  "leagueId": "7eab3e85-...",
  "queue": "RANKED_SOLO_5x5",
  "name": "LeBlanc's Brutes",
  "entries": [
    {
      "puuid": "2k19FKgI...",
      "leaguePoints": 2462,
      "rank": "I",
      "wins": 315,
      "losses": 266,
      "veteran": true,
      "inactive": false,
      "freshBlood": false,
      "hotStreak": false
    }
  ]
}
```
* **KR 기준 인원:** 약 300명 (2026-03-28 조회)

### [API-E] 그랜드마스터 리그 조회
* **URL:** `https://kr.api.riotgames.com/lol/league/v4/grandmasterleagues/by-queue/RANKED_SOLO_5x5`
* **인증:** 챌린저와 동일
* **응답 구조:** 챌린저와 동일 (`tier`만 `"GRANDMASTER"`로 다름)
* **KR 기준 인원:** 약 700명 (2026-03-28 조회)

### 공통 entry 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| `puuid` | String | 유저 고유 ID. Match-V5 등 다른 API에서 유저 식별에 사용 |
| `leaguePoints` | Number | LP (랭크 포인트) |
| `rank` | String | 항상 `"I"` (챌린저/그마는 단일 티어) |
| `wins` / `losses` | Number | 시즌 승/패 |
| `veteran` | Boolean | 오래 머문 유저 |
| `inactive` | Boolean | 비활성 유저 |
| `freshBlood` | Boolean | 최근 승급한 유저 |
| `hotStreak` | Boolean | 연승 중 |

> **수집 대상 합계:** 챌린저(~300) + 그랜드마스터(~700) = **약 1,000명**

---

## 4. Match-V5 API — 매치 데이터 조회

> Phase 2 데이터 수집 파이프라인의 두 번째 단계: 유저별 매치 ID 수집 및 타임라인 분석

### [API-F] 매치 ID 목록 조회 (by PUUID)
* **공식 문서:** https://developer.riotgames.com/apis#match-v5/GET_getMatchIdsByPUUID
* **URL:** `https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/{puuid}/ids`
* **리전:** KR 유저는 `asia` 라우팅 사용
* **인증:** `?api_key={RIOT_API_KEY}` 또는 `X-Riot-Token` 헤더

#### Query Parameters

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `startTime` | long | X | - | Epoch timestamp (초). 2021-06-16 이후 매치만 지원 |
| `endTime` | long | X | - | Epoch timestamp (초) |
| `queue` | int | X | - | 큐 ID 필터. 솔랭=`420`. `type`과 AND 조건으로 동작 |
| `type` | String | X | - | 매치 타입 필터. `queue`와 AND 조건으로 동작 |
| `start` | int | X | `0` | 시작 인덱스 |
| `count` | int | X | `20` | 반환할 매치 수. 유효 범위: 0~100 |

#### 응답
```json
["KR_8151787115", "KR_8151722805", "KR_8151100388", ...]
```
* matchId 문자열 배열. 형식: `{리전}_{숫자}`

#### queue / type 파라미터 참고
* `queue`: 게임 모드를 숫자 ID로 구분. `type`과 AND 조건으로 동작.
* 큐 ID 전체 목록: `https://static.developer.riotgames.com/docs/lol/queues.json`
* 파이프라인에서 사용하는 주요 큐 ID:

| queue ID | 설명 |
|----------|------|
| `420` | 5v5 Ranked Solo (솔로/듀오 랭크) |
| `440` | 5v5 Ranked Flex (자유 랭크) |

#### 파이프라인 사용 예시
* `queue=420` (솔랭) + `startTime`/`endTime`으로 하루치 매치만 수집
* `startTime`/`endTime`은 **Epoch seconds (Unix timestamp)** 기준, **매치 시작 시각**으로 필터링
* 배치 실행 시 전날 00:00~23:59를 지정하면 하루치 매치를 깔끔하게 수집 가능
```
예: startTime=1774569600 (2026-03-25 00:00:00 KST)
    endTime=1774655999   (2026-03-25 23:59:59 KST)
    queue=420, count=100
```

### [API-G] 매치 상세 조회
* **공식 문서:** https://developer.riotgames.com/apis#match-v5/GET_getMatch
* **URL:** `https://asia.api.riotgames.com/lol/match/v5/matches/{matchId}`
* **리전:** KR 매치는 `asia` 라우팅 사용
* **인증:** API-F와 동일
* **응답 크기:** 매우 큼 (참가자 10명 × 147개 필드). 필요한 필드만 파싱할 것.

#### 응답 구조 (최상위)

| 키 | 설명 |
|----|------|
| `metadata.matchId` | 매치 ID |
| `metadata.participants` | 참가자 puuid 배열 (10명) |
| `info.gameVersion` | 패치 버전 (예: `"16.6.756.9613"`) |
| `info.gameDuration` | 게임 시간 (초). 리메이크 필터링에 사용 |
| `info.gameStartTimestamp` | 게임 시작 시각 (Epoch **밀리초**) |
| `info.participants[]` | 참가자 상세 배열 (10명) |

#### 파이프라인에서 사용하는 participants 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| `puuid` | String | 유저 고유 ID |
| `riotIdGameName` | String | Riot ID 이름 (예: `"Hide on bush"`) |
| `riotIdTagline` | String | Riot ID 태그 (예: `"KR1"`) |
| `championName` | String | 챔피언 영문 ID (예: `"Hecarim"`) |
| `teamId` | Number | `100`=블루, `200`=레드 |
| `teamPosition` | String | 포지션. **정글러 필터링은 `"JUNGLE"` 사용** |
| `win` | Boolean | 승리 여부. 추후 승률 통계에 활용 |
| `perks` | Object | 룬 정보. 주 룬 트리(4개) + 부 룬 트리(2개) + 스탯 파편. ID 매핑은 API-C2 참고 |
| `summoner1Id` / `summoner2Id` | Number | 소환사 주문 ID. ID 매핑은 API-C3 참고 |

> **포지션 판별:** `individualPosition`(개인 행동 기반 추측)과 `teamPosition`(팀 구성 제약 기반 추측) 두 필드가 있다. Riot 공식 권장은 **`teamPosition`** 사용.

> **`summonerName`은 폐기됨** — 빈 문자열 반환. `riotIdGameName` + `riotIdTagline`을 사용할 것.

> **kills/deaths/assists는 게임 전체 합산값**이므로 "4레벨 전 개입 여부" 판단에는 사용 불가. Timeline API에서 시점별로 확인해야 함.

#### 파이프라인 활용
1. `teamPosition === "JUNGLE"`로 매치당 정글러 2명 추출
2. `gameDuration`으로 리메이크(극단적으로 짧은 게임) 필터링
3. 챔피언, 팀, 패치 버전 등 메타데이터 수집
4. 상세 타임라인 분석은 Timeline API(API-H)에서 수행

### [API-H] 매치 타임라인 조회
* **공식 문서:** https://developer.riotgames.com/apis#match-v5/GET_getTimeline
* **URL:** `https://asia.api.riotgames.com/lol/match/v5/matches/{matchId}/timeline`
* **리전:** KR 매치는 `asia` 라우팅 사용
* **인증:** API-F와 동일
* **응답 크기:** 매우 큼. 필요한 이벤트/필드만 파싱할 것.

#### 응답 구조 (최상위)

| 키 | 설명 |
|----|------|
| `metadata.matchId` | 매치 ID |
| `metadata.participants` | 참가자 puuid 배열 (10명). 배열 인덱스+1 = participantId |
| `info.frameInterval` | 프레임 간격 (ms). 기본 `60000` (1분) |
| `info.frames[]` | 시간별 프레임 배열. 각 프레임에 `timestamp`, `events[]`, `participantFrames` 포함 |
| `info.participants[]` | participantId ↔ puuid 매핑 |

#### participantFrames (1분 간격 스냅샷)

각 프레임마다 참가자 10명의 상태를 기록. 키는 participantId 문자열 (`"1"` ~ `"10"`).

| 필드 | 타입 | 설명 |
|------|------|------|
| `participantId` | Number | 참가자 ID (1~10) |
| `position` | {x, y} | 맵 내 좌표. **1분 시점 좌표로 시작 캠프 판별에 사용** |
| `level` | Number | 현재 레벨 |
| `xp` | Number | 현재 경험치 |
| `currentGold` | Number | 보유 골드 |
| `totalGold` | Number | 누적 골드 |
| `jungleMinionsKilled` | Number | 정글 몬스터 처치 수 (누적) |
| `minionsKilled` | Number | 라인 미니언 처치 수 (누적) |
| `championStats` | Object | 챔피언 스탯 24개 (체력, 공격력, 방어력, 이속 등) |
| `damageStats` | Object | 피해량 통계 12개 (물리/마법/고정, 챔피언 대상 등) |
| `timeEnemySpentControlled` | Number | CC기 적중 시간 |
| `goldPerSecond` | Number | 초당 골드 |

#### 이벤트 타입 (events[])

##### 파이프라인 핵심 이벤트

| 이벤트 | 필드 | 파이프라인 용도 |
|--------|------|----------------|
| `LEVEL_UP` | `participantId`, `level`, `timestamp`(ms) | **4레벨 달성 시점 추출** |
| `CHAMPION_KILL` | `killerId`, `victimId`, `assistingParticipantIds[]`, `timestamp`, `position`, `bounty` | **4레벨 전 개입(킬/어시/데스) 여부 판별** |

##### 부가 이벤트

| 이벤트 | 필드 | 설명 |
|--------|------|------|
| `SKILL_LEVEL_UP` | `participantId`, `skillSlot`(1~4), `levelUpType` | 스킬 레벨업 순서 |
| `ITEM_PURCHASED` | `participantId`, `itemId`, `timestamp` | 아이템 구매 |
| `ITEM_DESTROYED` | `participantId`, `itemId`, `timestamp` | 아이템 소모/파괴 |
| `ELITE_MONSTER_KILL` | `killerId`, `killerTeamId`, `monsterType`, `position` | 엘리트 몬스터(드래곤/바론/그럽) 처치. 일반 정글몹은 해당 없음 |
| `WARD_PLACED` | `creatorId`, `wardType` | 와드 설치 |

> **참고:** 일반 정글 몬스터(버프, 늑대, 칼날부리 등) 개별 처치 이벤트는 타임라인에 기록되지 않는다. 정글 CS는 `participantFrames.jungleMinionsKilled`로 1분 간격 누적값만 확인 가능.

#### 파이프라인 활용 흐름
1. Match Detail API(API-G)에서 `teamPosition === "JUNGLE"`인 참가자의 `puuid` 확인
2. Timeline `metadata.participants` 배열에서 해당 puuid의 인덱스 → `participantId` 매핑
3. `frames[1].participantFrames[participantId].position`으로 **1분 시점 위치 → 시작 캠프 판별**
4. `LEVEL_UP` 이벤트에서 해당 participantId의 **level === 4 도달 timestamp 추출**
5. `CHAMPION_KILL` 이벤트에서 해당 timestamp 이전에 **killerId/victimId/assistingParticipantIds에 포함되면 폐기**

