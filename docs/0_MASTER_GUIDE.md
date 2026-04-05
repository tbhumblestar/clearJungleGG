# 0_MASTER_GUIDE.md (JungleClear.gg 프로젝트 마스터 가이드)

## 1. 프로젝트 목적 및 AI 역할
* **프로젝트명:** JungleClear.gg
* **당신의 역할:** 프로젝트 전반을 담당하는 AI. 프론트엔드(Next.js 컴포넌트), 백엔드(데이터 수집/정제), 기획(문서 관리) 등 사용자의 지시에 따라 작업을 수행함.
* **단일 진실 공급원 (SSOT):** 개발 중 의문이 생기면 반드시 아래 문서를 최우선으로 참고할 것.
  * `1_PRD.md` — 기능 기획
  * `frontend/DESIGN_SYSTEM.md` — 디자인 시스템 및 컴포넌트 셋 (색상, 타이포, 패턴, 컴포넌트 Props)
  * `design/color_v2/DESIGN.md` — 디자인 원칙

## 2. 기술 스택 (Tech Stack)

### Frontend
* **프레임워크:** Next.js 16 (App Router) + TypeScript
* **스타일링:** Tailwind CSS v4 (CSS-first config, `globals.css`의 `@theme inline` 블록에서 토큰 정의)
* **상태 관리:** React Hooks (`useState`, `useEffect` 등 기본 훅 위주로 경량화)
* **아이콘/폰트:** Material Symbols Outlined, Space Grotesk(헤드라인), Manrope(본문), JetBrains Mono(숫자/데이터)
* **배포:** Vercel (Hobby/Free Tier)

### Backend
* **언어:** Kotlin
* **프레임워크:** Spring WebFlux (Netty)
* **비동기:** Kotlin Coroutines + WebClient
* **ORM:** Spring Data JPA (Hibernate)
* **DB:** PostgreSQL (Supabase Free Tier)
* **배포:** AWS EC2 (Free Tier, t2.micro)
* **구현 규칙:** `4_BACKEND_IMPLEMENTATION.md` 참고

### 인프라
* 상세 구성은 `5_DEPLOYMENT_ARCHITECTURE.md` 참고

## 3. 문서 구조

### 문서 작성 규칙
1. **PRD(`1_PRD.md`)는 유저 경험만 서술한다.** "유저가 무엇을 보고, 어떤 행동을 하고, 어떤 결과를 얻는가." 컴포넌트명, CSS 클래스, 기술적 구조 등 구현 세부사항은 쓰지 않는다.
2. **프론트엔드 구현서(`3_FRONTEND_IMPLEMENTATION.md`)는 화면 구현 방법을 서술한다.** PRD의 유저 경험 항목에 1:1로 대응하여, 어떤 컴포넌트/데이터/상태 관리로 화면을 실현하는지를 정의한다.
3. **ROADMAP(`7_ROADMAP.md`)은 프로젝트의 큰 방향을 정의한다.** 각 Phase의 목표와 핵심 결과물을 서술한다. 로드맵을 진행하면서 보류한 기능은 `10_BACKLOG.md`에 쌓는다.
4. **새 기능 추가 시 순서:** ROADMAP에서 현재 Phase 확인 → PRD에 유저 경험 정의 → 프론트엔드 구현서에 화면 구현 방법 추가 → 코드 작성.

### 문서 목록

#### 기획 문서
| 문서 | 역할 | 언제 참고하는가 |
|------|------|----------------|
| `0_MASTER_GUIDE.md` | 프로젝트 마스터 가이드 (본 문서) | 모든 작업의 시작점. 가장 먼저 읽을 것 |
| `1_PRD.md` | 기능 정의서 (유저 경험 중심) | 새 기능 추가, 기존 동작 변경 시 |
| `7_ROADMAP.md` | 프로젝트 로드맵 (Phase별 목표) | 다음에 무엇을 해야 하는지 판단할 때 |
| `4_BACKEND_IMPLEMENTATION.md` | 백엔드 구현 지시서 (데이터 수집/처리 구현 방법) | 백엔드 구현, 배치 로직 확인 시 |
| `10_BACKLOG.md` | 보류된 기능 목록 | Phase 진행 중 보류한 기능을 확인하거나 꺼내올 때 |

#### 기술 문서
| 문서 | 역할 | 언제 참고하는가 |
|------|------|----------------|
| `2_API_SPEC.md` | API 명세서 (요청/응답 JSON 스키마) | 백엔드 연동, mock 데이터 확장 시 |
| `3_FRONTEND_IMPLEMENTATION.md` | 프론트엔드 구현 지시서 (화면 구현 방법) | 화면 구현, 컴포넌트 구조 확인 시 |
| `5_DEPLOYMENT_ARCHITECTURE.md` | 인프라 및 배포 설계서 | 배포 환경 변경, 인프라 확장 시 |
| `6_RIOT_API_GUIDE.md` | Riot API 가이드 | 챔피언 이미지 URL, 데이터 파이프라인 구현 시 |
| `8_LOL_DOMAIN_KNOWLEDGE.md` | LoL 도메인 지식 (스폰 타임, 게임 메커니즘 등) | 타임라인 분석 기준값, 정글 캠프 관련 로직 구현 시 |
| `9_DATA_STRATEGY.md` | 데이터 전략서 (수집, 정제, 저장 기준) | 백엔드 데이터 로직 구현, 새로운 데이터 수집 항목 추가 시 |
| `frontend/DESIGN_SYSTEM.md` | 디자인 시스템 및 컴포넌트 셋 | UI 수정, 새 컴포넌트 구현 시. Stitch 등 외부 AI 도구에게도 이 문서를 제공할 것 |

## 4. 환경 변수 (`.env`)

| 변수명 | 설명 | 비고 |
|--------|------|------|
| `RIOT_API_KEY` | Riot Games API 키 | 데이터 수집 배치에서 사용 |

> `.env` 파일은 `.gitignore`에 포함되어 Git에 커밋되지 않습니다.

## 5. AI 협업 및 코딩 규칙 (Rule of Engagement)

### Frontend
1. **디자인 시스템을 따를 것:** 새 컴포넌트를 만들 때 `frontend/DESIGN_SYSTEM.md`의 토큰, 패턴, 유틸리티 클래스를 우선 사용할 것. 기존에 없는 스타일이 필요하면 디자인 시스템에 추가한 후 사용할 것.
2. **Primary 색상:** `#10B981` (primary), `#0DA573` (primary-container). 그라디언트: `jungle-gradient` 클래스 사용.
3. **컴포넌트 주도 개발 (CDD):** 큰 페이지를 한 번에 짜지 말고, `components/` 폴더 내에 재사용 가능한 단위로 잘게 쪼개서 작업할 것.
4. **더미 데이터(Mock Data) 활용:** 백엔드 API가 아직 연결되지 않았으므로, `src/data/mock.ts`의 기존 데이터 구조를 참고하여 확장할 것.

### Backend
1. **API Spec 기반 TDD:** `2_API_SPEC.md`에 API를 먼저 정의하고 → 해당 스펙에 맞는 테스트 코드를 작성한 뒤 → 테스트를 통과시키는 방식으로 API를 구현한다.
2. **하이브리드 I/O 규칙 준수:** `4_BACKEND_IMPLEMENTATION.md`에 정의된 `Dispatchers.IO` 격리, `@Transactional` 동기 함수, 단방향 호출 규칙을 반드시 따를 것.
