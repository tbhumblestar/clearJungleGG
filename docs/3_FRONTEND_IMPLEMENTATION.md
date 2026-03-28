# 3_FRONTEND_IMPLEMENTATION.md (프론트엔드 구현 지시서)

## 🚀 [Phase 1] 메인 페이지 구현 ✅ 완료

> PRD 참고: [화면 1] 메인 페이지

**구현 목표:** PRD의 "메인 페이지" 유저 경험을 코드로 실현한다.

### 1-1. 공통 레이아웃 구현
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| 고정 내비게이션 바 (로고 + 메뉴 + 인증 버튼) | `components/layout/GNB.tsx` — fixed top, glass 배경. 모바일에서 메뉴 `hidden md:flex` |
| 하단 푸터 | `components/layout/Footer.tsx` — 로고 + 태그라인 + 카피라이트 |
| 루트 레이아웃 | `app/layout.tsx` — Google Fonts(3종 + Material Symbols) CDN 로드, `<html lang="ko" className="dark">` |

### 1-2. Hero + 검색 구현
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| "MASTER THE JUNGLE" 타이틀 + 서브카피 | `components/home/HeroSection.tsx` — `text-5xl md:text-7xl` 반응형 |
| 검색창 입력 → 자동완성 드롭다운 | `components/home/SearchBar.tsx` — `useState`로 query/isOpen 관리, `championNameKo` 필터링, 외부 클릭 시 닫힘 |
| 영상 있는 챔피언 상단 활성 / 없는 챔피언 하단 비활성 분리 | `components/home/SearchDropdown.tsx` — `withVideo`/`withoutVideo` 배열 분리 렌더링 |
| 검색 결과: 썸네일 + 이름 + 승률 + Best 타임 + Watch Video | `components/home/SearchItem.tsx` — `hasVideo` 여부로 활성/비활성 분기. 활성: `<Link>`로 상세 이동. 비활성: grayscale, "영상 준비중" |

### 1-3. 챔피언 그리드 구현
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| 인기순 챔피언 카드 나열 (영상 있는 챔피언만) | `components/home/ChampionGrid.tsx` — `hasVideo` 필터 + `popularityRank` 정렬, `grid-cols-2 sm:3 md:4 lg:5` |
| 카드: Splash 이미지 + 이름 + 승률 + Best 타임 | `components/home/ChampionCard.tsx` — `aspect-[16/11]`, hover play_circle 오버레이 |
| 카드 클릭 → 상세 페이지 이동 | `<Link href="/champions/[id]">` 래핑 |

### 1-4. 데이터
* `src/data/mock.ts` — `2_API_SPEC.md`의 [API 1] 구조 기준. 35개 챔피언 (hasVideo:true) + 2개 (hasVideo:false).
* 이미지: Riot Data Dragon CDN 직접 사용 (`6_RIOT_API_GUIDE.md` 참고).

---

## 🚀 [Phase 2] 상세 페이지 구현 ✅ 완료

> PRD 참고: [화면 2] 챔피언 상세 페이지

**구현 목표:** PRD의 "상세 페이지" 유저 경험을 코드로 실현한다.

### 2-1. 페이지 구조
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| 좌측(챔피언 정보) : 우측(영상) 2분할 | `app/champions/[id]/page.tsx` — `grid-cols-12`, 좌 `col-span-4`, 우 `col-span-8`. 모바일: 세로 스택 |
| 존재하지 않는 챔피언 접근 시 404 | `notFound()` 호출 |

### 2-2. 좌측 패널 — 챔피언 정보
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| Splash 아트 위에 이름 + 이명 겹쳐 표시 | `components/detail/ChampionMetaPanel.tsx` — `aspect-[4/3]` + 하단 그라디언트 오버레이 + absolute 텍스트 |
| WIN/PICK/BAN RATE 3가지 통계 | `components/detail/StatBox.tsx` × 3 — variant별 색상 (primary/white/error) |
| 최단 클리어 시간 + 패치 버전 | `components/detail/ClearTimeCard.tsx` — 좌측 primary 보더 강조, 모노폰트 시간 |
| 티어 순위 (예: #4 Jungle) | `components/detail/TierRankCard.tsx` |

### 2-3. 우측 — 영상 플레이어 + 리더보드
| PRD 유저 경험 | 구현 방법 |
|-------------|----------|
| 1위 영상 자동 로드 + RECORD HOLDER 뱃지 | `components/detail/VideoPlayer.tsx` — YouTube iframe 16:9 임베드, `rank === 1`일 때 뱃지 노출 |
| 리더보드: 순위/소환사명/티어/시간/패치 | `components/detail/VideoListItem.tsx` — 티어별 뱃지 스타일 (challenger/grandmaster/master) |
| 현재 재생 항목 시각적 강조 | `isActive` prop → primary 보더 + `pulse-border` + 녹색 순위 텍스트 |
| 다른 항목 클릭 → 영상 전환 | `useState(selectedVideoIndex)` → `onSelect` 콜백으로 인덱스 변경 → VideoPlayer 갱신 |
| 영상 1개 → 리더보드 숨김 | `components/detail/VideoList.tsx` — `videos.length <= 1` 이면 `return null` |

### 2-4. 데이터
* `src/data/mock.ts` — `2_API_SPEC.md`의 [API 2] 구조 기준. 35개 챔피언 상세 데이터 + 실제 YouTube 영상 ID.
