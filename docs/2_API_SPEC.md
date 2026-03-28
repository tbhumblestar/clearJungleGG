# 2_API_SPEC.md (JungleClear.gg API 명세서)

## 💡 공통 규칙
* **Base URL:** `/api/v1`
* **Response Type:** `application/json`

## [API 1] 메인 페이지: 챔피언 통합 목록 조회

* **Endpoint:** `GET /api/v1/champions`
* **Description:** 메인 페이지의 '인기 챔피언' 섹션, 챔피언 그리드 뷰 및 검색 자동완성에 필요한 전체 챔피언 요약 통계와 완성된 초상화 이미지 URL을 배열 형태로 반환합니다.
* **Query Parameters:**
  * `sort` (String, Optional): 정렬 기준 (`SPEED`: 클리어 빠른순(기본), `WIN_RATE`: 승률순, `NAME`: 가나다순)
  * `tier` (String, Optional): 티어 필터 (`ALL`(기본), `CHALLENGER`, `GRANDMASTER`, `MASTER`)

* **Response (Success 200 OK):**
```json
{
  "status": 200,
  "message": "챔피언 목록 조회 성공",
  "data": [
    {
      "championId": "LeeSin",
      "championNameKo": "리신",
      "portraitUrl": "https://ddragon.leagueoflegends.com/cdn/16.6.1/img/champion/LeeSin.png",
      "splashUrl": "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/LeeSin_0.jpg",
      "bestClearTime": "03:05",
      "bestClearTimeMs": 185000,
      "winRate": 51.2,
      "pickRate": 15.4,
      "tierRank": 1,
      "popularityRank": 1,
      "hasVideo": true
    },
    {
      "championId": "Nidalee",
      "championNameKo": "니달리",
      "portraitUrl": "https://ddragon.leagueoflegends.com/cdn/16.6.1/img/champion/Nidalee.png",
      "splashUrl": "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Nidalee_0.jpg",
      "bestClearTime": "02:58",
      "bestClearTimeMs": 178000,
      "winRate": 49.8,
      "pickRate": 12.1,
      "tierRank": 1,
      "popularityRank": 2,
      "hasVideo": true
    },
    {
      "championId": "Teemo",
      "championNameKo": "티모",
      "portraitUrl": "https://ddragon.leagueoflegends.com/cdn/16.6.1/img/champion/Teemo.png",
      "splashUrl": "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Teemo_0.jpg",
      "bestClearTime": null,
      "bestClearTimeMs": null,
      "winRate": 48.1,
      "pickRate": 1.2,
      "tierRank": 5,
      "popularityRank": 45,
      "hasVideo": false
    }
  ]
}
```

## [API 2] 상세 페이지: 챔피언 메타데이터 및 리더보드 조회

* **Endpoint:** `GET /api/v1/champions/{championId}`
* **Description:** 특정 챔피언의 상세 메타데이터(통계, 완성된 이미지 URL 포함)와 풀캠프 클리어 타임 TOP 10 영상 리더보드를 한 번에 반환합니다.
* **Path Variable:**
  * `championId` (String, Required): 챔피언 고유 ID (예: `LeeSin`)
* **Query Parameters:**
  * `patch` (String, Optional): 특정 패치 버전 필터링 (예: `16.6`). 값이 없으면 전체 버전 기준의 랭킹을 반환합니다.
  * `region` (String, Optional): 서버 지역 필터링 (`KR`(기본), `GLOBAL`)

* **Response (Success 200 OK):**
```json
{
  "status": 200,
  "message": "챔피언 상세 정보 조회 성공",
  "data": {
    "championMeta": {
      "championId": "LeeSin",
      "championNameKo": "리신",
      "title": "눈먼 수도승",
      "portraitUrl": "https://ddragon.leagueoflegends.com/cdn/16.6.1/img/champion/LeeSin.png",
      "splashUrl": "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/LeeSin_0.jpg",
      "tierRank": 1,
      "winRate": 51.2,
      "pickRate": 15.4,
      "banRate": 8.2,
      "bestClearTime": "03:05",
      "bestClearTimeMs": 185000,
      "bestClearPatch": "16.6"
    },
    "videos": [
      {
        "rank": 1,
        "clearTime": "03:05",
        "clearTimeMs": 185000,
        "patchVersion": "16.6",
        "summonerName": "Hide on bush",
        "summonerTier": "CHALLENGER",
        "youtubeVideoId": "dQw4w9WgXcQ"
      },
      {
        "rank": 2,
        "clearTime": "03:08",
        "clearTimeMs": 188000,
        "patchVersion": "16.5",
        "summonerName": "Canyon",
        "summonerTier": "CHALLENGER",
        "youtubeVideoId": "aB1cD2eF3gH"
      },
      {
        "rank": 3,
        "clearTime": "03:10",
        "clearTimeMs": 190000,
        "patchVersion": "16.6",
        "summonerName": "Peanut",
        "summonerTier": "GRANDMASTER",
        "youtubeVideoId": "jK3lM4nO5pQ"
      }
    ]
  }
}
```
