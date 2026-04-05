# 4_BACKEND_IMPLEMENTATION.md (백엔드 구현 지시서)

> `9_DATA_STRATEGY.md`의 데이터 전략을 어떤 코드/모듈/배치 로직으로 실현하는지를 정의하는 문서입니다.
> `3_FRONTEND_IMPLEMENTATION.md`가 PRD의 화면을 구현하는 지시서라면, 이 문서는 데이터 전략을 구현하는 지시서입니다.

---

## 1. 기술 스택

| 항목 | 선택 | 비고 |
|------|------|------|
| 언어 | Kotlin | |
| 웹 프레임워크 | Spring WebFlux (Netty) | 넌블로킹 서버 |
| 비동기 처리 | Kotlin Coroutines (`suspend`, `Flow`) | |
| HTTP 클라이언트 | WebClient | Riot API 넌블로킹 호출 |
| ORM | Spring Data JPA (Hibernate) | 블로킹 I/O |
| DB | PostgreSQL (Supabase Free Tier) | |
| 배포 | AWS EC2 t2.micro (1 vCPU, 1GB RAM) | |

---

## 2. 핵심 아키텍처: 하이브리드 I/O

이 프로젝트는 **넌블로킹(WebClient) + 블로킹(JPA)** 을 한 서버에서 함께 사용하는 하이브리드 I/O 구조를 채택한다. t2.micro의 제한된 리소스에서 Riot API 대량 호출을 처리하기 위한 선택이다.

### 2-1. 두 개의 I/O 구역

| 구역 | 실행 환경 | 역할 | 특징 |
|------|-----------|------|------|
| **비동기/넌블로킹** | Netty EventLoop | Riot API 호출 (WebClient), 흐름 제어 | `suspend` 함수로 스레드 점유 없이 수백 건 동시 처리 |
| **동기/블로킹** | `Dispatchers.IO` | DB 저장/조회 (JPA) | 반드시 `withContext(Dispatchers.IO)`로 격리 |

### 2-2. 절대 규칙

**규칙 1: JPA 호출은 반드시 `Dispatchers.IO`로 격리한다.**

JPA는 본질적으로 블로킹 I/O이다. EventLoop 스레드에서 직접 JPA를 호출하면 서버 전체가 멈춘다.

```kotlin
// ✅ 올바른 패턴
suspend fun processMatch(matchId: String) {
    val data = webClient.get()...  // 넌블로킹 Riot API 호출

    withContext(Dispatchers.IO) {
        matchRepository.save(data)  // 블로킹 JPA → IO 스레드에서 실행
    }
}

// ❌ 금지 — EventLoop 블로킹
suspend fun processMatch(matchId: String) {
    val data = webClient.get()...
    matchRepository.save(data)  // EventLoop에서 직접 JPA 호출 → 서버 멈춤
}
```

**규칙 2: `@Transactional` 서비스는 동기 함수로 작성한다.**

코루틴의 스레드 이동(Context Switching)으로 JPA의 ThreadLocal이 유실되는 것을 방지한다.

```kotlin
// ✅ 올바른 패턴 — 트랜잭션 서비스는 일반 함수
@Service
class MatchJpaService(private val matchRepository: MatchRepository) {

    @Transactional
    fun saveMatchRecord(record: MatchRecord) {  // suspend 아님
        matchRepository.save(record)
    }
}

// 코루틴 레이어에서 호출
suspend fun handleNewRecord(record: MatchRecord) {
    withContext(Dispatchers.IO) {
        matchJpaService.saveMatchRecord(record)  // IO 스레드 안에서 동기 실행
    }
}
```

**규칙 3: 호출 방향은 항상 단방향이다.**

```
코루틴(suspend) → withContext(Dispatchers.IO) → JPA 서비스(동기)
```

JPA 서비스에서 코루틴 레이어를 역으로 호출하지 않는다.

---

## 3. 구현 Phase

*(Phase 2 백엔드 구현 시 모듈별 상세 작성 예정)*
