-- ============================================================
-- JungleClear.gg Database Schema
-- PostgreSQL (Supabase)
-- 참조 문서: docs/9_DATA_STRATEGY.md [DS-2]
-- ============================================================

-- ------------------------------------------------------------
-- 1. champion: 챔피언 메타데이터 (Data Dragon 동기화)
-- ------------------------------------------------------------
CREATE TABLE champion (
    id            VARCHAR(50)  NOT NULL,
    key           INT          NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    title         VARCHAR(100) NOT NULL,
    patch_version VARCHAR(20)  NOT NULL,
    champion_history JSONB     NOT NULL DEFAULT '[]'::jsonb,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_champion PRIMARY KEY (id)
);

COMMENT ON TABLE champion IS '챔피언 메타데이터. Data Dragon에서 패치당 1회 동기화하며, 변경 이력을 champion_history에 누적 기록한다.';
COMMENT ON COLUMN champion.id IS '챔피언 영문 고유 ID. Data Dragon 기준 (예: LeeSin, Graves). URL 경로 및 이미지 조립에 사용.';
COMMENT ON COLUMN champion.key IS '챔피언 숫자 식별자. 일부 Riot API에서 숫자 ID로 참조 시 사용 (예: 64 = LeeSin).';
COMMENT ON COLUMN champion.name IS '챔피언 한글 공식 이름 (예: 리 신). 띄어쓰기 포함.';
COMMENT ON COLUMN champion.title IS '챔피언 이명 (예: 눈먼 수도승). 상세 페이지 표시용.';
COMMENT ON COLUMN champion.patch_version IS '마지막으로 동기화된 Data Dragon 패치 버전 (예: 16.6).';
COMMENT ON COLUMN champion.champion_history IS '변경 이력 JSON 배열. 패치 동기화 시 기존 값과 비교하여 변경점이 있으면 추가. 구조: [{patch, changed_at, changes: {필드: {before, after}}}]';
COMMENT ON COLUMN champion.updated_at IS '마지막 동기화 시각.';

-- ------------------------------------------------------------
-- 2. match_record: 정글 매치 원천 기록 (SSOT)
-- ------------------------------------------------------------
CREATE TABLE match_record (
    id                   BIGSERIAL    NOT NULL,
    match_id             VARCHAR(30)  NOT NULL,
    champion_id          VARCHAR(50)  NOT NULL,
    team                 VARCHAR(4)   NOT NULL,
    win                  BOOLEAN      NOT NULL,
    opponent_champion_id VARCHAR(50)  NOT NULL,
    runes                JSONB        NOT NULL,
    summoner_spells      VARCHAR(10)  NOT NULL,
    start_position       VARCHAR(20),
    banned_champions     JSONB        NOT NULL DEFAULT '[]'::jsonb,
    patch_version        VARCHAR(20)  NOT NULL,
    summoner_name        VARCHAR(50)  NOT NULL,
    summoner_tag         VARCHAR(10)  NOT NULL,
    summoner_tier        VARCHAR(20),
    game_started_at      TIMESTAMPTZ  NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_match_record PRIMARY KEY (id),
    CONSTRAINT uq_match_record_match_champion UNIQUE (match_id, champion_id),
    CONSTRAINT fk_match_record_champion FOREIGN KEY (champion_id) REFERENCES champion(id),
    CONSTRAINT fk_match_record_opponent FOREIGN KEY (opponent_champion_id) REFERENCES champion(id),
    CONSTRAINT ck_match_record_team CHECK (team IN ('BLUE', 'RED'))
);

CREATE INDEX idx_match_record_champion ON match_record(champion_id);
CREATE INDEX idx_match_record_matchup ON match_record(champion_id, opponent_champion_id);
CREATE INDEX idx_match_record_patch ON match_record(patch_version);

COMMENT ON TABLE match_record IS '정글 매치 원천 기록 (Single Source of Truth). 천상계 매치에서 정글러 2명의 기록을 각각 1행씩, 매치당 총 2행을 저장한다.';
COMMENT ON COLUMN match_record.id IS 'Auto increment PK.';
COMMENT ON COLUMN match_record.match_id IS 'Riot 매치 고유 ID (예: KR_8151787115). API-F에서 수집.';
COMMENT ON COLUMN match_record.champion_id IS '해당 정글러가 플레이한 챔피언 ID. champion.id FK.';
COMMENT ON COLUMN match_record.team IS '소속 팀. BLUE(teamId=100) 또는 RED(teamId=200).';
COMMENT ON COLUMN match_record.win IS '해당 정글러 소속 팀의 승리 여부.';
COMMENT ON COLUMN match_record.opponent_champion_id IS '상대 팀 정글러의 챔피언 ID. 상성(matchup) 집계에 사용. champion.id FK.';
COMMENT ON COLUMN match_record.runes IS '룬 세팅 JSON. API-G participants[].perks 원본 저장. 주 룬 트리(4개) + 부 룬 트리(2개) + 스탯 파편.';
COMMENT ON COLUMN match_record.summoner_spells IS '소환사 주문 2개의 ID를 콤마로 구분 (예: 11,4 = 강타+점멸). API-G의 summoner1Id, summoner2Id.';
COMMENT ON COLUMN match_record.start_position IS '1분 시점 시작 캠프. 타임라인(API-H) 분석 후 채움. NULL이면 아직 분석 전. 값: RED_BUFF, BLUE_BUFF, RAPTORS 등.';
COMMENT ON COLUMN match_record.banned_champions IS '해당 매치에서 밴된 챔피언 ID 배열 (양 팀 합산, 최대 10개). 매치당 2행에 동일 값이 들어감 (비정규화, SSOT 우선).';
COMMENT ON COLUMN match_record.patch_version IS '게임이 진행된 패치 버전 (예: 16.6). API-G info.gameVersion 앞 2자리.';
COMMENT ON COLUMN match_record.summoner_name IS '플레이어 Riot ID 이름. API-G participants[].riotIdGameName.';
COMMENT ON COLUMN match_record.summoner_tag IS '플레이어 Riot ID 태그. API-G participants[].riotIdTagline (예: KR1).';
COMMENT ON COLUMN match_record.summoner_tier IS '데이터 수집 시점의 플레이어 티어. CHALLENGER, GRANDMASTER 등. 수집 대상 목록(API-D, E)에 없는 정글러는 NULL.';
COMMENT ON COLUMN match_record.game_started_at IS '게임 시작 시각. API-G info.gameStartTimestamp(Epoch ms)를 변환하여 저장.';
COMMENT ON COLUMN match_record.created_at IS '레코드 생성 시각.';

-- ------------------------------------------------------------
-- 3. champion_stats: 챔피언별 통계 집계 (파생)
-- ------------------------------------------------------------
CREATE TABLE champion_stats (
    champion_id   VARCHAR(50)  NOT NULL,
    patch_version VARCHAR(20)  NOT NULL,
    pick_count    INT          NOT NULL DEFAULT 0,
    win_count     INT          NOT NULL DEFAULT 0,
    ban_count     INT          NOT NULL DEFAULT 0,
    total_matches INT          NOT NULL DEFAULT 0,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_champion_stats PRIMARY KEY (champion_id, patch_version),
    CONSTRAINT fk_champion_stats_champion FOREIGN KEY (champion_id) REFERENCES champion(id)
);

COMMENT ON TABLE champion_stats IS '챔피언별 통계 집계 테이블. match_record 기반으로 배치에서 패치별로 집계한다.';
COMMENT ON COLUMN champion_stats.champion_id IS '챔피언 ID. champion.id FK.';
COMMENT ON COLUMN champion_stats.patch_version IS '통계 대상 패치 버전.';
COMMENT ON COLUMN champion_stats.pick_count IS '해당 패치에서 정글로 픽된 횟수. 픽률 = pick_count / total_matches.';
COMMENT ON COLUMN champion_stats.win_count IS '해당 패치에서 정글로 픽되어 승리한 횟수. 승률 = win_count / pick_count.';
COMMENT ON COLUMN champion_stats.ban_count IS '해당 패치에서 밴된 횟수. match_record.banned_champions에서 match_id 중복 제거 후 집계. 밴률 = ban_count / total_matches.';
COMMENT ON COLUMN champion_stats.total_matches IS '해당 패치의 전체 분석 매치 수. 픽률과 밴률의 분모.';
COMMENT ON COLUMN champion_stats.updated_at IS '마지막 집계 시각.';

-- ------------------------------------------------------------
-- 4. champion_matchup: 챔피언 간 상성 (파생)
-- ------------------------------------------------------------
CREATE TABLE champion_matchup (
    champion_id          VARCHAR(50)  NOT NULL,
    opponent_champion_id VARCHAR(50)  NOT NULL,
    patch_version        VARCHAR(20)  NOT NULL,
    wins                 INT          NOT NULL DEFAULT 0,
    losses               INT          NOT NULL DEFAULT 0,
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_champion_matchup PRIMARY KEY (champion_id, opponent_champion_id, patch_version),
    CONSTRAINT fk_champion_matchup_champion FOREIGN KEY (champion_id) REFERENCES champion(id),
    CONSTRAINT fk_champion_matchup_opponent FOREIGN KEY (opponent_champion_id) REFERENCES champion(id)
);

COMMENT ON TABLE champion_matchup IS '정글 챔피언 간 1:1 상성 테이블. match_record 기반으로 배치에서 집계. 양방향 자동 생성 (리신 vs 그레이브즈, 그레이브즈 vs 리신 각각 존재).';
COMMENT ON COLUMN champion_matchup.champion_id IS '기준 챔피언 ID. 이 챔피언 입장에서의 승률을 기록. champion.id FK.';
COMMENT ON COLUMN champion_matchup.opponent_champion_id IS '상대 챔피언 ID. champion.id FK.';
COMMENT ON COLUMN champion_matchup.patch_version IS '통계 대상 패치 버전.';
COMMENT ON COLUMN champion_matchup.wins IS '기준 챔피언이 상대 챔피언을 만났을 때 승리한 횟수.';
COMMENT ON COLUMN champion_matchup.losses IS '기준 챔피언이 상대 챔피언을 만났을 때 패배한 횟수. 승률 = wins / (wins + losses).';
COMMENT ON COLUMN champion_matchup.updated_at IS '마지막 집계 시각.';

-- ------------------------------------------------------------
-- 5. clear_record: 순수 풀캠프 클리어 기록 (파생)
-- ------------------------------------------------------------
CREATE TABLE clear_record (
    id               BIGSERIAL   NOT NULL,
    match_record_id  BIGINT      NOT NULL,
    clear_time_ms    INT         NOT NULL,
    youtube_video_id VARCHAR(20),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_clear_record PRIMARY KEY (id),
    CONSTRAINT fk_clear_record_match_record FOREIGN KEY (match_record_id) REFERENCES match_record(id),
    CONSTRAINT uq_clear_record_match_record UNIQUE (match_record_id)
);

CREATE INDEX idx_clear_record_time ON clear_record(clear_time_ms);

COMMENT ON TABLE clear_record IS '순수성 검증(Purity Check)을 통과한 풀캠프 클리어 기록. 영상 녹화 후보이자 리더보드 데이터. match_record에서 파생.';
COMMENT ON COLUMN clear_record.id IS 'Auto increment PK.';
COMMENT ON COLUMN clear_record.match_record_id IS '원천 매치 기록 FK. match_record.id 참조. 1:1 관계 (하나의 매치 기록에 최대 1개의 클리어 기록).';
COMMENT ON COLUMN clear_record.clear_time_ms IS '4레벨 달성 시간 (밀리초, 게임 내 시간 기준). 예: 185000 = 3분 5초. 타임라인 LEVEL_UP 이벤트에서 추출.';
COMMENT ON COLUMN clear_record.youtube_video_id IS 'YouTube 영상 ID. Phase 3에서 녹화 및 업로드 후 채움. NULL이면 아직 영상 없음.';
COMMENT ON COLUMN clear_record.created_at IS '레코드 생성 시각.';
