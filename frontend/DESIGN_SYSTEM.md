# JungleClear.gg Design System & Component Set

> 이 문서는 현재 구현된 프론트엔드 코드 기반으로 정리한 디자인 시스템입니다.
> Stitch 등 AI 도구나 개발자가 새 컴포넌트/페이지를 만들 때 이 문서를 참고하세요.

---

## 1. Design Tokens

### 1-1. Colors

Material Design 3 기반의 다크 테마 색상 체계입니다. `globals.css`의 `@theme inline` 블록에서 정의됩니다.

| 역할 | 토큰명 | 값 | 용도 |
|------|--------|-----|------|
| **Primary** | `primary` | `#10B981` | 메인 강조색 (CTA, 활성 상태, 기록 수치) |
| | `primary-container` | `#0DA573` | 그라디언트 끝점, 보조 강조 |
| | `on-primary` | `#005D27` | Primary 위의 텍스트 |
| | `on-primary-container` | `#002C0F` | Primary Container 위의 텍스트 (Sign Up 버튼 등) |
| **Error** | `error` | `#FF7351` | Ban Rate 등 부정적 수치 |
| **Surface** | `background` / `surface` | `#0A0E17` | 페이지 배경 |
| | `surface-container` | `#141A26` | 카드, 드롭다운, 입력 필드 배경 |
| | `surface-container-high` | `#1A1F2C` | 호버 상태, 활성 리스트 아이템 배경 |
| | `surface-container-highest` | `#202633` | 패치 버전 뱃지 배경 |
| **Text** | `on-surface` | `#EBEDFB` | 기본 텍스트 (제목, 이름, 수치) |
| | `on-surface-variant` | `#A7ABB7` | 보조 텍스트 (레이블, 부가 정보) |
| **Border** | `outline` | `#727581` | 기본 테두리 |
| | `outline-variant` | `#444852` | 약한 테두리 |

**실제 테두리는 대부분 `border-white/5` 또는 `border-white/10`을 사용합니다.**

### 1-2. Typography

| 용도 | 폰트 | Tailwind 클래스 | 사용처 |
|------|-------|----------------|--------|
| 헤드라인 / 레이블 | Space Grotesk | `font-headline` | 로고, 섹션 제목, 버튼 레이블, 수치 |
| 본문 | Manrope | `font-body` (기본) | 일반 텍스트, 설명 |
| 숫자 / 데이터 | JetBrains Mono | `font-mono` | 클리어 타임, 수치 데이터 |

**크기 패턴:**
- 페이지 타이틀: `text-5xl md:text-7xl font-headline font-bold tracking-tighter italic uppercase`
- 섹션 제목: `text-2xl font-headline font-bold uppercase tracking-tight`
- 카드 제목: `text-sm font-headline font-bold`
- 레이블: `text-[10px] uppercase tracking-widest font-bold`
- 본문: `text-sm` 또는 `text-xs`

### 1-3. Icons

**Material Symbols Outlined** (Google Fonts CDN)

```html
<span class="material-symbols-outlined">icon_name</span>
```

자주 쓰는 아이콘:
- `search` — 검색
- `play_circle` — 영상 재생
- `pause_circle` — 재생 중
- `emoji_events` — 트로피 (1위 뱃지)

### 1-4. Spacing & Layout

| 요소 | 값 |
|------|-----|
| 최대 너비 | `max-w-[1440px] mx-auto` |
| 페이지 좌우 패딩 | `px-8` |
| 카드 내부 패딩 | `p-3` ~ `p-4` |
| 그리드 간격 | `gap-4 md:gap-6` |
| 섹션 간 간격 | `space-y-4` |
| 컴포넌트 간 간격 | `space-y-2` ~ `space-y-3` |

### 1-5. Responsive Breakpoints

| 요소 | Mobile (<640) | Tablet (640-1024) | Desktop (≥1024) |
|------|--------------|-------------------|-----------------|
| GNB 메뉴 | 숨김 | 노출 (`md:flex`) | 노출 |
| Hero 타이틀 | `text-5xl` | — | `text-7xl` |
| 챔피언 그리드 | 2열 | 3~4열 | 5열 |
| 상세 레이아웃 | 세로 스택 | — | 좌4:우8 split |

---

## 2. Utility Classes

`globals.css`에 정의된 커스텀 유틸리티 클래스입니다.

### 2-1. jungle-gradient
```css
.jungle-gradient {
  background: linear-gradient(135deg, #10B981 0%, #0DA573 100%);
}
```
**용도:** Sign Up 버튼 등 주요 CTA 배경

### 2-2. glass-panel
```css
.glass-panel {
  background: rgba(20, 26, 38, 0.8);
  backdrop-filter: blur(20px);
}
```
**용도:** 현재 GNB에서는 직접 `bg-[#0A0E17]/80 backdrop-blur-xl` 사용

### 2-3. pulse-shadow
```css
.pulse-shadow:hover {
  box-shadow: 0 0 25px rgba(16, 185, 129, 0.12);
}
```
**용도:** 챔피언 카드 호버 시 은은한 primary glow

### 2-4. pulse-border
```css
.pulse-border {
  box-shadow: 0 0 0 1px rgba(16, 185, 129, 0.3);
}
```
**용도:** 리더보드에서 현재 재생 중인 아이템 강조

### 2-5. Tier Badge Classes
```css
.tier-badge-challenger  /* 금-빨강 그라디언트 텍스트 */
.tier-badge-grandmaster /* 빨강-주황 그라디언트 텍스트 */
.tier-badge-master      /* 보라색 텍스트 (#C084FC) */
```
**용도:** 리더보드 소환사 티어 표시

---

## 3. Common Patterns

### 3-1. 카드 스타일
```
bg-surface-container rounded-xl border border-white/5
```
호버가 필요한 경우 `hover:bg-surface-container-high transition-all` 추가

### 3-2. 섹션 제목 (Primary bar + Title)
```jsx
<div className="flex items-center gap-4">
  <span className="w-12 h-[2px] bg-primary" />
  <h2 className="text-2xl font-headline font-bold text-on-surface uppercase tracking-tight">
    Section Title
  </h2>
</div>
```

### 3-3. GNB (글로벌 내비게이션)
- Fixed top, `z-50`
- 반투명 배경: `bg-[#0A0E17]/80 backdrop-blur-xl`
- 높이: `h-16`
- 하단 보더: `border-b border-white/5`
- 그림자: `shadow-[0_0_20px_rgba(16,185,129,0.06)]`

### 3-4. 이미지 처리
- **Portrait (정사각):** `relative w-10 h-10` 래퍼 + `<Image fill className="object-cover rounded-lg" />`
- **Splash (가로형):** `relative aspect-[16/11]` 또는 `aspect-[4/3]` 래퍼 + `<Image fill className="object-cover" />`
- **이미지 위 텍스트:** 하단 그라디언트 오버레이 `bg-gradient-to-t from-black/80 via-black/20 to-transparent`

### 3-5. 버튼 스타일
- **Primary CTA:** `jungle-gradient text-on-primary-container font-bold px-6 py-2 rounded-lg`
- **Ghost:** `text-white/70 hover:text-white px-4 py-2`
- **공통:** `transition-all active:scale-95`

### 3-6. 입력 필드
```
bg-surface-container border border-white/10 rounded-xl py-3.5 pl-12 pr-4
text-on-surface placeholder:text-on-surface-variant/50
outline-none focus:border-primary/50 focus:shadow-[0_0_0_2px_rgba(16,185,129,0.1)]
```

### 3-7. 드롭다운 / 팝오버
```
bg-surface-container border border-white/10 rounded-xl shadow-2xl z-50
max-h-[400px] overflow-y-auto
```

### 3-8. 뱃지
- **Record Holder:** `bg-primary/10 text-primary text-xs font-bold px-3 py-1 rounded-full border border-primary/20`
- **Patch Version:** `bg-surface-container-highest text-on-surface-variant px-1.5 py-0.5 rounded text-[10px]`

---

## 4. Component Set

### 4-1. Layout Components

| 컴포넌트 | 파일 | Props | 설명 |
|---------|------|-------|------|
| **GNB** | `components/layout/GNB.tsx` | 없음 | 고정 상단 내비게이션. 로고 + 메뉴(Champions, Leaderboards) + 인증 버튼 |
| **Footer** | `components/layout/Footer.tsx` | 없음 | 하단 푸터. 로고 + 태그라인 + 카피라이트 |

### 4-2. Home Page Components

| 컴포넌트 | 파일 | Props | 설명 |
|---------|------|-------|------|
| **HeroSection** | `components/home/HeroSection.tsx` | `champions: Champion[]` | 메인 타이틀 + 서브타이틀 + SearchBar |
| **SearchBar** | `components/home/SearchBar.tsx` | `champions: Champion[]` | 검색 입력 필드. `championNameKo` 기준 필터링. 외부 클릭 시 드롭다운 닫힘 |
| **SearchDropdown** | `components/home/SearchDropdown.tsx` | `results: Champion[]` | 검색 결과 드롭다운. 영상 있는 챔피언 상단, 없는 챔피언 하단 분리 |
| **SearchItem** | `components/home/SearchItem.tsx` | `champion: Champion` | 검색 결과 개별 항목. hasVideo 여부에 따라 활성/비활성 상태 |
| **ChampionGrid** | `components/home/ChampionGrid.tsx` | `champions: Champion[]` | 5열 반응형 그리드. hasVideo만 표시, popularityRank 정렬 |
| **ChampionCard** | `components/home/ChampionCard.tsx` | `champion: Champion` | 개별 챔피언 카드. Splash 이미지 + 이름 + 승률 + Best 타임 |

### 4-3. Detail Page Components

| 컴포넌트 | 파일 | Props | 설명 |
|---------|------|-------|------|
| **ChampionMetaPanel** | `components/detail/ChampionMetaPanel.tsx` | `meta: ChampionMeta` | 좌측 패널. Splash + 이름 오버레이 + StatBox×3 + ClearTimeCard + TierRankCard |
| **StatBox** | `components/detail/StatBox.tsx` | `label: string, value: string, variant?: "primary"\|"white"\|"error"` | WIN/PICK/BAN RATE 개별 박스 |
| **ClearTimeCard** | `components/detail/ClearTimeCard.tsx` | `time: string, patch: string` | 최단 클리어 시간. 좌측 primary 보더 강조 |
| **TierRankCard** | `components/detail/TierRankCard.tsx` | `rank: number` | "#N Jungle" 형태의 티어 순위 |
| **VideoPlayer** | `components/detail/VideoPlayer.tsx` | `video: Video, championName: string` | YouTube 16:9 임베드 + RECORD HOLDER 뱃지 + 기록 표시 |
| **VideoList** | `components/detail/VideoList.tsx` | `videos: Video[], selectedIndex: number, onSelect: (i) => void` | 리더보드 목록. 영상 1개일 경우 렌더링 안 함 |
| **VideoListItem** | `components/detail/VideoListItem.tsx` | `video: Video, isActive: boolean, onSelect: () => void` | 리더보드 개별 항목. 순위 + 소환사명 + 티어뱃지 + 클리어타임 + 패치뱃지 |

---

## 5. Data Types

```typescript
/** 챔피언 목록 아이템 */
interface Champion {
  championId: string;        // "LeeSin"
  championNameKo: string;    // "리신"
  portraitUrl: string;       // Riot Data Dragon portrait URL
  splashUrl: string;         // Riot Data Dragon splash URL
  bestClearTime: string | null; // "03:05" 형식
  bestClearTimeMs: number | null;
  winRate: number;           // 52.4
  pickRate: number;          // 12.8
  tierRank: number;          // 1~N
  popularityRank: number;    // 그리드 정렬 기준
  hasVideo: boolean;         // false면 검색결과에서 비활성
}

/** 챔피언 상세 메타데이터 */
interface ChampionMeta {
  championId: string;
  championNameKo: string;
  title: string;             // "눈먼 수도승" 등 이명
  portraitUrl: string;
  splashUrl: string;
  tierRank: number;
  winRate: number;
  pickRate: number;
  banRate: number;           // 상세에서만 노출
  bestClearTime: string;
  bestClearTimeMs: number;
  bestClearPatch: string;    // "14.10" 등
}

/** 영상 리더보드 아이템 */
interface Video {
  rank: number;
  clearTime: string;         // "03:05"
  clearTimeMs: number;
  patchVersion: string;      // "14.10"
  summonerName: string;
  summonerTier: "CHALLENGER" | "GRANDMASTER" | "MASTER";
  youtubeVideoId: string;    // YouTube embed에 사용
}

/** 챔피언 상세 응답 (Meta + Videos) */
interface ChampionDetail {
  championMeta: ChampionMeta;
  videos: Video[];
}
```

---

## 6. Image Sources

모든 챔피언 이미지는 **Riot Data Dragon CDN**에서 직접 로드합니다. 서버 트래픽 없음.

- **Portrait (정사각):** `https://ddragon.leagueoflegends.com/cdn/15.6.1/img/champion/{id}.png`
- **Splash (가로):** `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/{id}_0.jpg`

`next.config.ts`에서 `images.remotePatterns`에 `ddragon.leagueoflegends.com` 등록 필요.

---

## 7. Page Structure

### 메인 페이지 (`/`)
```
<GNB />
<main pt-16>              ← GNB 높이만큼 패딩
  <HeroSection>
    <SearchBar>
      <SearchDropdown>
        <SearchItem />
      </SearchDropdown>
    </SearchBar>
  </HeroSection>
  <ChampionGrid>
    <ChampionCard />
  </ChampionGrid>
</main>
<Footer />
```

### 상세 페이지 (`/champions/[id]`)
```
<GNB />
<main pt-16 grid-cols-12>
  <aside col-span-4>
    <ChampionMetaPanel>
      <StatBox /> × 3
      <ClearTimeCard />
      <TierRankCard />
    </ChampionMetaPanel>
  </aside>
  <section col-span-8>
    <VideoPlayer />
    <VideoList>
      <VideoListItem />
    </VideoList>
  </section>
</main>
<Footer />
```
