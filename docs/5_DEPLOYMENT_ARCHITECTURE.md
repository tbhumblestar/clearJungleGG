# 5_DEPLOYMENT_ARCHITECTURE.md (JungleClear.gg 인프라 및 배포 설계서)

## 1. 전체 시스템 아키텍처 (System Architecture)
초기 운영 비용 최소화(Zero-Cost)와 트래픽 분산 처리에 최적화된 하이브리드 클라우드 아키텍처를 채택합니다.

* **Frontend:** Next.js (App Router) → **Vercel (Hobby/Free Tier)**
* **Backend / Batch:** Spring WebFlux (Kotlin) → **AWS EC2 (Free Tier, t2.micro)**
  * 배치 스케줄러(Riot API 데이터 수집)도 동일 서버에서 Spring Scheduler로 구동
  * Kotlin Coroutines + WebClient로 넌블로킹 대량 API 호출
* **Database:** PostgreSQL → **Supabase (Free Tier)**
* **Media Hosting:**
  * 이미지: Riot Data Dragon CDN
  * 동영상: YouTube Iframe API

---

## 2. 인프라 요소별 상세 구성

### A. 프론트엔드 (Vercel)
* **역할:** Next.js 애플리케이션 호스팅, SSR/SSG 렌더링, Edge Network를 통한 정적 자원(HTML, JS, CSS) 배포.
* **배포 방식 (CI/CD):**
  * 모노레포(`clearJungleGG`) 구조. Vercel의 **Root Directory를 `frontend`로 설정**.
  * `main` 브랜치에 Push/Merge 발생 시 Vercel에서 자동으로 빌드 및 무중단 배포 수행.
  * PR(Pull Request) 생성 시 Preview URL 자동 생성.
* **네트워크 전략:** 외부 에셋(라이엇 이미지, 유튜브 영상)에 대한 트래픽을 프론트엔드 서버가 부담하지 않으므로, Vercel의 무료 티어(Hobby) 대역폭 한도 내에서 대규모 사용자 수용 가능.

### B. 백엔드 및 배치 파이프라인 (AWS EC2 Free Tier)
* **역할:** 프론트엔드 API 요청 처리(`GET /api/v1/champions` 등) 및 Riot API 데이터 수집 배치 작업.
* **인스턴스:** t2.micro (1 vCPU, 1GB RAM) — Free Tier 12개월 무료.
* **배치 스케줄러:** Spring Scheduler(@Scheduled)로 동일 서버에서 주기적 실행.
* **핵심 로직 (Riot Data Pipeline):**
  1. 주기적(예: 매시간)으로 Riot Match-V5 API 호출.
  2. 타임라인 이벤트를 분석하여 '3분 15초 풀캠프' 데이터 추출 및 검증.
  3. 검증된 최단 기록을 Supabase(DB)에 Insert/Update.
* **배포 방식:** 같은 모노레포에서 GitHub Actions를 활용. `backend/` 경로 변경 시에만 빌드 트리거.

### C. 데이터베이스 (Supabase)
* **역할:** 챔피언 메타데이터, 유저 기록, 리더보드 등 영구 데이터 저장.
* **선택 이유:** PostgreSQL 기반이며, 토이 프로젝트에 충분한 무료 티어 용량과 연결(Connection) 풀 제공.

---

## 3. 트래픽 오프로딩 (Traffic Offloading) 전략
대용량 미디어 트래픽을 외부 CDN으로 넘겨 서버 요금 폭탄을 방지합니다.

1. **이미지 (Riot Data Dragon):** API 응답 시 `https://ddragon.leagueoflegends.com/...` 형태의 완성된 URL만 전달. 프론트엔드는 이를 그대로 렌더링.
2. **동영상 (YouTube):** API 응답 시 `youtubeVideoId`만 전달. 프론트엔드는 Google 유튜브 서버에서 Iframe으로 직접 스트리밍.

---

## 4. CI/CD 파이프라인 요약
1. **Local Dev:** 개발자가 코드를 작성하고 GitHub에 Push.
2. **GitHub:** `main` 브랜치에 코드가 병합(Merge)됨.
3. **Frontend (Vercel):** GitHub Webhook을 감지하여 자동 배포.
4. **Backend (GitHub Actions):** `.github/workflows`에 정의된 스크립트가 실행되어 빌드, 테스트 후 EC2로 배포.
