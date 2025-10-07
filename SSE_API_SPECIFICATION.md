# SSE API 명세서

## 개요
본 문서는 Mozu 플랫폼의 Server-Sent Events (SSE) API 명세를 다룹니다. SSE를 통해 실시간으로 수업 진행 상황, 팀 참여, 투자 완료 등의 이벤트를 클라이언트에게 전달합니다.

## 인증
- **학생**: JWT 토큰 (Bearer Token)
- **관리자**: JWT 토큰 (Bearer Token)

## SSE 연결 엔드포인트

### 1. 팀 SSE 연결 (학생용)
```
GET /team/sse
Authorization: Bearer {JWT_TOKEN}
Accept: text/event-stream
```

**설명**: 학생이 팀 관련 실시간 이벤트를 수신하기 위한 SSE 연결

**응답 헤더**:
- `Content-Type: text/event-stream`
- `Cache-Control: no-cache`
- `Connection: keep-alive`

**초기 연결 이벤트**:
```
event: TEAM_SSE_CONNECTED
data: {"type":"TEAM_SSE_CONNECTED","message":"id {teamId}번의 학생 클라이언트 SSE 연결되었습니다."}
```

### 2. 수업 관리자 SSE 연결 (관리자용)
```
GET /lesson/sse/{lesson-id}
Authorization: Bearer {JWT_TOKEN}
Accept: text/event-stream
```

**설명**: 관리자가 수업 관련 실시간 이벤트를 수신하기 위한 SSE 연결

**응답 헤더**:
- `Content-Type: text/event-stream`
- `Cache-Control: no-cache`
- `Connection: keep-alive`

**초기 연결 이벤트**:
```
event: LESSON_SSE_CONNECTED
data: {"type":"LESSON_SSE_CONNECTED","message":"id {lessonId}의 수업 기관 클라이언트 SSE 연결되었습니다."}
```

### 3. 범용 SSE 연결
```
GET /sse/subscribe?clientId={string}
```

**설명**: 범용 SSE 연결 (현재 사용되지 않음, 레거시)

## 이벤트 유형

### 1. 팀 참여 이벤트 (관리자에게 전송)
**이벤트명**: `TEAM_PART_IN`

**발생 시점**: 새로운 팀이 수업에 참여할 때

**대상**: 관리자 SSE 연결 (`lesson-organ-sse:{lessonId}:{organId}`)

**데이터 구조**:
```json
{
  "teamId": "uuid",
  "teamName": "string",
  "schoolName": "string", 
  "lessonNum": "string"
}
```

**예시**:
```
event: TEAM_PART_IN
data: {"teamId":"550e8400-e29b-41d4-a716-446655440000","teamName":"투자왕팀","schoolName":"대덕소프트웨어마이스터고등학교","lessonNum":"LESSON001"}
```

### 2. 다음 차수 진행 이벤트 (학생에게 전송)
**이벤트명**: `CLASS_NEXT_INV_START`

**발생 시점**: 관리자가 다음 투자 차수를 시작할 때

**대상**: 팀 SSE 연결 (`team-sse:{teamId}`)

**데이터 구조**:
```json
{
  "lessonId": "uuid",
  "curInvRound": "number",
  "teamId": "uuid", 
  "teamName": "string",
  "schoolName": "string"
}
```

**예시**:
```
event: CLASS_NEXT_INV_START
data: {"lessonId":"550e8400-e29b-41d4-a716-446655440001","curInvRound":2,"teamId":"550e8400-e29b-41d4-a716-446655440000","teamName":"투자왕팀","schoolName":"대덕소프트웨어마이스터고등학교"}
```

### 3. 팀 투자 완료 이벤트 (관리자에게 전송)
**이벤트명**: `TEAM_INV_END`

**발생 시점**: 팀이 투자를 완료할 때

**대상**: 관리자 SSE 연결 (`lesson-organ-sse:{lessonId}:{organId}`)

**데이터 구조**:
```json
{
  "teamId": "uuid",
  "teamName": "string",
  "curInvRound": "number",
  "totalMoney": "number",
  "valuationMoney": "number", 
  "profitNum": "number"
}
```

**예시**:
```
event: TEAM_INV_END
data: {"teamId":"550e8400-e29b-41d4-a716-446655440000","teamName":"투자왕팀","curInvRound":2,"totalMoney":1150000,"valuationMoney":150000,"profitNum":15.0}
```

### 4. 수업 취소 이벤트 (학생에게 전송)
**이벤트명**: `CLASS_CANCEL`

**발생 시점**: 관리자가 수업을 종료할 때

**대상**: 팀 SSE 연결 (`team-sse:{teamId}`)

**데이터 구조**:
```json
{
  "classId": "uuid",
  "teamId": "uuid"
}
```

**예시**:
```
event: CLASS_CANCEL
data: {"classId":"550e8400-e29b-41d4-a716-446655440001","teamId":"550e8400-e29b-41d4-a716-446655440000"}
```

## 서비스 로직 플로우

### 팀 참여 플로우
1. 학생이 `POST /team/participate`로 팀 참여
2. `TeamParticipationService`에서 팀 생성
3. 트랜잭션 커밋 후 관리자 SSE에 `TEAM_PART_IN` 이벤트 발송

### 투자 완료 플로우
1. 학생이 `POST /team/end`로 투자 완료
2. `CompleteTeamInvestmentService`에서 주식 거래 처리
3. 트랜잭션 커밋 후 관리자 SSE에 `TEAM_INV_END` 이벤트 발송

### 다음 차수 진행 플로우
1. 관리자가 `PATCH /lesson/next/{lesson-id}` 호출
2. `NextLessonService`에서 `curInvRound` 업데이트
3. 트랜잭션 커밋 후 모든 팀 SSE에 `CLASS_NEXT_INV_START` 이벤트 발송

### 수업 종료 플로우
1. 관리자가 `PATCH /lesson/end/{lesson-id}` 호출
2. `EndLessonService`에서 수업 상태 업데이트
3. 트랜잭션 커밋 후 모든 팀 SSE에 `CLASS_CANCEL` 이벤트 발송

## 연결 상태 관리

### 연결 유지
- 클라이언트는 `EventSource` API를 사용하여 SSE 연결을 유지
- 서버에서 주기적으로 ping 이벤트 전송 (1초 간격)
- 연결이 끊어진 경우 자동 재연결 시도

### 에러 처리
- **401 Unauthorized**: JWT 토큰이 유효하지 않거나 만료됨
- **403 Forbidden**: 해당 리소스에 접근 권한이 없음  
- **404 Not Found**: 요청한 수업이나 팀이 존재하지 않음
- **500 Internal Server Error**: 서버 내부 오류

## 클라이언트 구현 예시

### JavaScript (EventSource)
```javascript
// 팀 SSE 연결
const eventSource = new EventSource('/team/sse', {
  headers: {
    'Authorization': `Bearer ${jwtToken}`
  }
});

// 이벤트 리스너 등록
eventSource.addEventListener('TEAM_SSE_CONNECTED', function(event) {
  console.log('팀 SSE 연결됨:', JSON.parse(event.data));
});

eventSource.addEventListener('CLASS_NEXT_INV_START', function(event) {
  const data = JSON.parse(event.data);
  console.log('다음 차수 시작:', data);
  // UI 업데이트 로직
});

eventSource.addEventListener('CLASS_CANCEL', function(event) {
  const data = JSON.parse(event.data);
  console.log('수업 취소됨:', data);
  // 수업 종료 처리
});

// 에러 처리
eventSource.onerror = function(event) {
  console.error('SSE 연결 오류:', event);
  // 재연결 로직
};
```

### React 구현 예시
```typescript
import { useEffect, useState } from 'react';

interface SSEHook {
  isConnected: boolean;
  lastEvent: any;
}

export const useTeamSSE = (token: string): SSEHook => {
  const [isConnected, setIsConnected] = useState(false);
  const [lastEvent, setLastEvent] = useState(null);

  useEffect(() => {
    const eventSource = new EventSource('/team/sse', {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    eventSource.onopen = () => setIsConnected(true);
    eventSource.onerror = () => setIsConnected(false);

    eventSource.addEventListener('CLASS_NEXT_INV_START', (event) => {
      setLastEvent(JSON.parse(event.data));
    });

    return () => eventSource.close();
  }, [token]);

  return { isConnected, lastEvent };
};
```

### 관리자 SSE 구현 예시
```javascript
// 관리자 SSE 연결
const lessonId = "550e8400-e29b-41d4-a716-446655440001";
const eventSource = new EventSource(`/lesson/sse/${lessonId}`, {
  headers: {
    'Authorization': `Bearer ${adminToken}`
  }
});

// 팀 참여 이벤트 처리
eventSource.addEventListener('TEAM_PART_IN', function(event) {
  const data = JSON.parse(event.data);
  console.log('새 팀 참여:', data);
  // 관리자 대시보드 업데이트
  addTeamToList(data);
});

// 팀 투자 완료 이벤트 처리
eventSource.addEventListener('TEAM_INV_END', function(event) {
  const data = JSON.parse(event.data);
  console.log('팀 투자 완료:', data);
  // 투자 현황 업데이트
  updateTeamInvestmentStatus(data);
});
```

## 주요 특징

1. **트랜잭션 안전성**: 모든 이벤트는 DB 트랜잭션 커밋 후 발송되어 데이터 일관성 보장
2. **인증 기반**: JWT 토큰을 통한 인증으로 보안성 확보
3. **역할 기반 라우팅**: 학생은 팀 이벤트, 관리자는 수업 이벤트 수신
4. **실시간 동기화**: 투자 진행 상황을 실시간으로 모든 클라이언트에 전달

## 주의사항

1. **인증**: 모든 SSE 연결은 유효한 JWT 토큰이 필요합니다.
2. **권한**: 학생은 자신의 팀 SSE에만, 관리자는 자신의 수업 SSE에만 연결할 수 있습니다.
3. **연결 관리**: 브라우저 탭 종료나 네트워크 단절 시 서버에서 자동으로 연결을 정리합니다.
4. **이벤트 순서**: 모든 이벤트는 트랜잭션 커밋 후 발송되어 데이터 일관성을 보장합니다.
5. **재연결**: 연결이 끊어진 경우 클라이언트에서 자동 재연결을 구현하는 것을 권장합니다.
6. **데이터 형식**: `profitNum`은 Double 타입으로 전송됩니다 (예: 15.0 = 15% 수익률).
