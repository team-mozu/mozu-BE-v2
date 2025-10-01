package team.mozu.dsm.domain.sse.model

/**
 * SSE 연결 상태
 */
enum class ConnectionStatus {
    CONNECTING, // 연결 중
    CONNECTED, // 연결됨
    RECONNECTING, // 재연결 중
    DISCONNECTED, // 연결 끊김
    ERROR, // 오류 상태
    TIMEOUT // 타임아웃
}
