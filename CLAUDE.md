# JungleClear.gg 프로젝트

## 세션 시작 시 필수 사항
작업을 시작하기 전에 반드시 `docs/0_MASTER_GUIDE.md`를 읽고 프로젝트 맥락을 동기화할 것.
모든 문서 작성 규칙, 기술 스택, 코딩 규칙은 MASTER_GUIDE에 정의되어 있다.

## 문서 참조 방식
MASTER_GUIDE의 문서 목록은 **Lazy Loading** 방식으로 참조한다. 세션 시작 시 전체 문서를 읽지 않고, 작업에 필요한 문서만 그때그때 읽는다.
- 프론트엔드 작업 → `3_FRONTEND_IMPLEMENTATION.md`, `frontend/DESIGN_SYSTEM.md`
- 백엔드 작업 → `4_BACKEND_IMPLEMENTATION.md`, `9_DATA_STRATEGY.md`
- 기능 기획 → `1_PRD.md`, `7_ROADMAP.md`
- 등등, MASTER_GUIDE의 "언제 참고하는가" 컬럼을 기준으로 판단

## 절대 규칙
- **DDL(CREATE, ALTER, DROP TABLE 등)은 절대 실행하지 않는다.** DB 스키마 변경은 반드시 개발자가 직접 수행한다. SQL 파일(`backend/sql/`)의 작성/수정도 개발자 지시 없이 임의로 하지 않는다.
