package team.mozu.dsm.domain.sse.model

/**
 * SSE 이벤트 타입을 정의하는 열거형
 */
enum class SseEventType {
    // 팀 관련 이벤트
    TEAM_PART_IN, // 팀 참여 알림
    CLASS_NEXT_INV_START, // 투자 라운드 시작
    TEAM_INV_END, // 팀 투자 완료

    // 관리자 이벤트
    ALL_FINISHED, // 모든 팀 완료

    // 시스템 이벤트
    CONNECTION_ESTABLISHED, // 연결 설정 완료
    HEARTBEAT, // 연결 상태 확인
    ERROR // 오류 발생
}
