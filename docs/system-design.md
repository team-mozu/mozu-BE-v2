# System Design — mozu-BE-v2

Runtime view of the backend. Pairs with [architecture.md](./architecture.md) (structural view) and [SSE_API_SPECIFICATION.md](./SSE_API_SPECIFICATION.md) (wire format).

## 1. Topology

```
┌─────────────────┐         ┌──────────────────────┐
│ admin (FE)      │ ── REST ────────────────▶      │
│  Vite SPA       │ ── SSE (lesson/sse/{id}) ──▶  │
└─────────────────┘                                │
                                                   │
┌─────────────────┐                                ▼
│ student (FE)    │ ── REST ────────────────▶ mozu-BE-v2
│  Vite SPA       │ ── SSE (team/sse) ──────▶  Spring Boot
└─────────────────┘                                │
                                                   │ JPA
                                                   ├──────▶ MySQL 8
                                                   │
                                                   │ Lettuce
                                                   ├──────▶ Redis 7
                                                   │
                                                   │ AWS SDK
                                                   └──────▶ S3 (mozu-server-bucket)
```

- Single backend instance (in-memory SSE emitter store — see §6).
- Staging: `https://mozu-v2-stag.dsmhs.kr` fronts the JVM. xquare PaaS가 `mozu-server-v2-stag` 앱으로 운영, develop 브랜치 자동 빌드 + ArgoCD rollout.
- 인프라(Cloudflare 또는 xquare ingress)는 SSE 연결을 **약 127초 idle timeout으로 강제 종료** — §6 참고.

## 2. Authentication

- Login: `POST /organ/login` (admin) or `POST /team/participate` (student) returns access + refresh JWT.
- Bearer token sent in `Authorization` on every subsequent REST call.
- For SSE, the JWT is also sent in `Authorization` (the FE uses `event-source-polyfill` because the native `EventSource` cannot set headers).
- Refresh path: `POST /organ/token/reissue`. The FE axios interceptor auto-replays a 401/403 request once after refresh; SSE connections must be re-opened by the FE on refresh.

## 3. Request paths (golden flows)

### 3.1 Admin creates a lesson

```
admin → POST /lesson                        CreateLessonService    [Tx]
                                                                   commits Lesson + LessonItems + LessonArticles
       ◀── 201 {lessonId}
```

### 3.2 Admin starts a lesson

```
admin → POST /lesson/{id}/start             StartLessonService     [Tx]
                                                                   marks lesson READY, prepares round 0
                                                                   after commit:
                                                                     PublishToAllSseService → team channel
                                                                       event = CLASS_START
       ◀── 200
```

Critical ordering: **publish must run after the @Transactional method returns** so subscribers do not observe pre-commit state. Use `@TransactionalEventListener(AFTER_COMMIT)` or call the publisher from outside the transactional method.

### 3.3 Admin advances to next round

```
admin → PATCH /lesson/next/{id}             NextLessonService      [Tx]
                                                                   - commandLessonPort.updateCurInvRound
                                                                   - queryTeamPort.findAllByLessonId
                                                                   - afterCommit:
                                                                       팀별 publishSsePort.publishTo(
                                                                         "team-sse:{teamId}", CLASS_NEXT_INV_START,
                                                                         { lessonId, curInvRound, teamId, teamName, schoolName }
                                                                       )
       ◀── 204 No Content
```

주의: **admin emitter pool에는 발행되지 않음**. admin은 `useNextDegree` mutation의 onSuccess + React Query cache invalidate로 갱신. admin SSE의 `CLASS_NEXT_INV_START` 리스너는 dead path 상태.

### 3.4 Student participates

```
student → POST /team/participate {lessonNum,teamName,schoolName}    TeamParticipationService [Tx]
                                                                   - findByLessonNum, validate (organ, dup, in_progress, deleted)
                                                                   - insert tbl_team (cashMoney=baseMoney, isInvestmentInProgress=true)
                                                                   - afterCommit:
                                                                       publishToSseUseCase.publishTo(
                                                                         "lesson-organ-sse:{lessonId}:{organId}", "TEAM_PART_IN",
                                                                         { teamId, teamName, schoolName, lessonNum }
                                                                       )
        ◀── 201 { accessToken, refreshToken, accessExpiredAt, refreshExpiredAt }
student → GET  /team/sse  + Last-Event-ID(opt)   ConnectTeamSSEService
                                                                   - teamId from StudentPrincipal
                                                                   - subscribeWithRecovery: Redis Streams의 sse:team:{teamId}에서 lastEventId 이후 이벤트 replay
                                                                   - emit TEAM_SSE_CONNECTED
```

### 3.5 Student places / completes an order

```
student → POST /team/end [CompleteInvestmentRequest...]   CompleteTeamInvestmentService [Tx]
                                                                   - validateItems
                                                                   - 매수/매도 반영 (cash/stock/order_item 갱신)
                                                                   - afterCommit:
                                                                       팀별 publishTo("lesson-organ-sse:{lessonId}:{organId}",
                                                                         "TEAM_INV_END",
                                                                         { teamId, teamName, curInvRound, totalMoney, valuationMoney, profitNum }
                                                                       )
        ◀── 201
```

### 3.6 Admin monitoring (참여 대기 + 진행 중)

admin 모니터링은 두 source의 merge:

1. **SSE 즉시 알림** — `TEAM_PART_IN`, `TEAM_INV_END`로 zustand store(`teamInfoMap`)에 push
2. **REST 폴링 백업** — `GET /lesson/{id}/teams` (10초 간격, `useGetParticipatingTeams`):
   ```
   admin → GET /lesson/{id}/teams       QueryParticipatingTeamsService
                                                                   - organId 소유 검증
                                                                   - findAllByLessonId → ParticipatingTeamResponse[]
   ```
   응답 schema: `[{ teamId, teamName, schoolName, lessonNum, isInvestmentInProgress, totalMoney, cashMoney, valuationMoney }]`
   FE는 폴링 결과를 truth로 쓰고 SSE는 알림 + 즉시 갱신 hint.

### 3.7 Admin SSE 재연결 (with recovery)

```
admin → GET /lesson/sse/{id}  + Last-Event-ID(opt)  LessonOrganSSEService
                                                                   - clientId = "lesson-organ-sse:{lessonId}:{organId}"
                                                                   - lastEventId가 있으면 ssePersistenceAdapter.subscribeWithRecovery
                                                                     → Redis Streams의 sse:client:lesson-organ-sse:* 에서 누락 이벤트 replay
                                                                   - emit LESSON_SSE_CONNECTED
```

이 흐름은 8245c3aa 커밋에서 추가됨. 이전엔 admin SSE가 `Last-Event-ID` 무시 → 끊긴 사이 발생한 `TEAM_PART_IN`/`TEAM_INV_END`가 silent 손실.

## 4. SSE Channels

See [SSE_API_SPECIFICATION.md](./SSE_API_SPECIFICATION.md) for the full event catalog. Summary:

| Channel | Key shape | Subscribers |
|---------|-----------|-------------|
| `lesson-organ-sse:{lessonId}:{organId}` | one per lesson per admin | admin monitoring page |
| `team-sse:{teamId}` | one per student team | student app |
| `generic` (`clientId`) | legacy | not used by FE |

Lifecycle hooks on each emitter: `onCompletion`, `onTimeout`, `onError` → all remove the emitter from the in-memory store.

## 5. State stores

- **MySQL** — system of record (lesson, item, article, organ, team, order_item, etc.).
- **Redis** — short-lived state. Confirm in `global/config/redis/` and adapter code which keys live there (likely: refresh-token blacklist or rate counters).
- **S3** — binary assets only.
- **JVM heap** — SSE emitters, in `SseEmitterRepository`. **Lost on restart.** Clients reconnect on `onerror` and replay nothing (no Last-Event-ID handling yet — verify).

## 6. Failure modes & current gaps

### 6.1 운영 중 실제 발생 사고 (2026-05-13)

1. **`createdAt cannot be null` 500** — `@EnableJpaAuditing` 누락. `MozuBeV2Application`에 추가 + `Team.createdAt` nullable화로 해결 (`5665a0bd`, `6474dafa`).
2. **로그인 후 admin 화면 안 넘어감** — `useAdminLogin.onSuccess`에서 중복 `setCookies`가 prod cookie 정책에서 throw → `navigate("/class-management")` 누락. 중복 제거로 해결 (`7dcb9da4`).
3. **5명 중 1명 admin 대기 화면 미표시** — 인프라 127초 idle timeout으로 admin SSE 끊긴 사이 `TEAM_PART_IN` publish → emitter pool 비어 silent 손실. admin `Last-Event-ID` 복구 (`8245c3aa`) + 10초 폴링 백업 (`c0cdca2f` BE, `a8f77845` FE)으로 해결.
4. **'team' 팀 `TEAM_INV_END` 미표시** — SSE 손실 + admin teamInfoMap이 SSE를 single source of truth로 사용 → 새로고침해도 회복 불가. 폴링 백업으로 `isInvestmentInProgress=false` 팀의 trade 길이 보강 (`a8f77845`).

### 6.2 알려진 구조적 gap

- **Single-instance assumption.** Horizontal scale-out breaks SSE fan-out because emitters are local. 인프라(`RedisConfig.redisCommands`) 준비는 됐지만 pub/sub fan-out 코드는 없음. Multi-instance 시 깨짐.
- **Publish-before-commit.** 모든 publish는 `afterCommit`을 통과 — `TransactionSynchronizationManager.registerSynchronization(... afterCommit { publish })` 패턴 일관적. 신규 코드도 같은 패턴 강제 필요.
- **Inbound infrastructure idle timeout = 127초.** 정확히 127초 cycle로 SSE 강제 종료 측정됨 (2026-05-13 운영 로그). BE 측 `SseEmitter`는 1시간 timeout, `SseScheduler.sendPing` 30초 주기로 `: ping` comment 전송 중. 일부 proxy는 comment를 keep-alive로 안 보므로 실 이벤트(`name=heartbeat`)로 변경 검토.
- **admin SSE는 1회 끊기면 lastEventId 없이 재연결되면 누락 영구 손실.** 우리 fix는 FE가 lastEventId 보낼 때만 동작. **페이지 신규 진입 / 새로고침에는 lastEventId 없음** → 폴링 백업이 유일한 안전망.
- **`tbl_team`의 자산 컬럼 3개 동기화** — `total_money = cash_money + valuation_money` 불변식이 DB constraint로 강제되지 않음. 매 update 시 셋 다 같이 써야 깨지지 않음.
- **`tbl_lesson` 상태가 boolean 3개 분산** — `is_in_progress`/`is_deleted`/`is_starred` + `cur_inv_round`로 5+ 상태 표현. enum 단일 컬럼으로 정규화 권장 (architecture §9.3).
- **monitoring 데이터의 single source of truth가 SSE에만 존재** — 폴링 endpoint 추가됐지만 round별 history는 없음. `tbl_round_snapshot` 같은 history 테이블 검토.
- **JWT expiry vs long SSE.** FE는 axios 401에 reissue, `mozu-access-token-changed` 이벤트 발행, SSE는 새 토큰으로 재연결. fix됨 (`6e3d02ab`). 하지만 reissue 자체가 SSE 연결 도중 동시발생하면 race 가능.
- **CORS pattern broadness.** `allowedOriginPatterns` includes `http://192.168.*.*:*` 등. On-prem 운영 시 의도된 것. 클라우드 prod에서는 좁혀야 함.

## 7. Deployment

### 7.1 토폴로지

- **BE staging**: `mozu-server-v2-stag` 앱 (xquare PaaS).
  - 트리거: `team-mozu/mozu-BE-v2@develop` 푸시 → 자동 빌드 → ArgoCD sync → rollout
  - 가시성: `xquare app status mozu-server-v2-stag -p mozu`, `xquare logs mozu-server-v2-stag -p mozu --follow`
  - DB: `mozu-mozu-server-v2-stag-mysql` addon, tunneling: `xquare addon tunnel ... --local-port 13306`
  - 강제 재배포: `xquare trigger mozu-server-v2-stag -p mozu --watch`
- **FE prod**: `mozu-fe-admin-v2` / `mozu-fe-student-v2` (Vercel).
  - 트리거: `jidohyun/mozu-FE@main` 푸시 → Vercel 자동 빌드 (note: `team-mozu/mozu-FE`는 별개 리포)
  - 도메인: `mozu-admin.vercel.app`, `mozu-student.vercel.app`
  - Preview deployment 검증: 브랜치 푸시 → Vercel preview URL (SSO 보호) → `mcp__vercel__get_access_to_vercel_url`로 share token 발급

### 7.2 마이그레이션

- Flyway 자동, baseline 1. `V{n}__*.sql` 형태. 셔야 한번 배포된 V는 절대 수정 금지.
- `@EnableJpaAuditing` 필수 (`MozuBeV2Application`). 누락 시 `created_at` 인서트 실패.

### 7.3 Secrets

- `.env` (DB, JWT secret, AWS keys, S3 bucket). staging은 xquare env vars로 주입.

## 8. Open questions / TODOs

- **Multi-instance 지원**: `RedisConfig.redisCommands`는 Streams XADD/XRANGE에 사용 중이지만 emitter fan-out용 pub/sub은 미구현. 인스턴스 늘리려면 `RedisMessageListenerContainer` + `ChannelTopic("sse:fanout")` 추가 필요.
- **Round별 snapshot table**: 현재 `tbl_team`이 마지막 상태만 보존. round별 history가 필요하면 `tbl_round_snapshot (lesson_id, team_id, round, ...)` 신설.
- **Lesson 상태 정규화**: boolean 3개 + cur_inv_round → enum `LessonStatus` 단일 컬럼.
- **`tbl_lesson_item` round 가격 정규화**: `round1price..round5price` 컬럼 → `(item_id, lesson_id, round, price)` row.
- **`POST /lesson/next` idempotency**: 빠른 중복 클릭 시 두 번 진행될 가능성. FE에서 `inFlightRef` 가드 추가됐으나 BE도 lesson version 확인이나 멱등 키 검토.
- **`EndLessonService` emitter complete**: 학생/admin emitter `complete()` 명시 호출 (이미 `c731b9ba`에서 추가).
- **인프라 ingress idle timeout 조정**: 학교 PaaS 담당에게 600s 이상으로 변경 요청 중 (architecture §9.4 Phase 5).

## Related docs

- [architecture.md](./architecture.md) — structural view
- [SSE_API_SPECIFICATION.md](./SSE_API_SPECIFICATION.md) — wire format
- `../CLAUDE.md` — agent session brief
