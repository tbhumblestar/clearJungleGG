# JungleClear.gg Frontend

천상계 유저들의 가장 빠른 정글 풀캠프 클리어 데이터와 영상을 제공하는 웹 서비스입니다.

## Tech Stack

- **Framework:** Next.js 16 (App Router) + TypeScript
- **Styling:** Tailwind CSS v4
- **Fonts:** Space Grotesk / Manrope / JetBrains Mono
- **Icons:** Material Symbols
- **Deployment:** Vercel

## Getting Started

```bash
npm install
npm run dev
```

http://localhost:3000 에서 확인할 수 있습니다.

## Pages

- `/` — 메인 페이지 (챔피언 검색 + 인기 챔피언 그리드)
- `/champions/[id]` — 챔피언 상세 페이지 (메타데이터 + 영상 리더보드)

## Project Structure

```
src/
├── app/                  # Next.js App Router 페이지
│   ├── page.tsx          # 메인 페이지
│   └── champions/[id]/   # 챔피언 상세 페이지
├── components/
│   ├── layout/           # GNB, Footer
│   ├── home/             # 메인 페이지 컴포넌트
│   └── detail/           # 상세 페이지 컴포넌트
├── data/
│   └── mock.ts           # Mock 데이터 (35개 챔피언 + 실제 YouTube 영상)
└── types/
    └── champion.ts       # TypeScript 인터페이스
```
