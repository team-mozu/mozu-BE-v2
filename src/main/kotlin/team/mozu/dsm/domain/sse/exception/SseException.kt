package team.mozu.dsm.domain.sse.exception

import team.mozu.dsm.domain.sse.model.SseEventType

/**
 * SSE 관련 예외의 기본 클래스
 */
sealed class SseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {

    /**
     * SSE 연결이 닫혔을 때 발생하는 예외
     */
    class ConnectionClosedException(connectionId: String) : SseException("SSE connection closed: $connectionId")

    /**
     * 잘못된 연결 상태일 때 발생하는 예외
     */
    class InvalidConnectionStateException(message: String) : SseException("Invalid SSE connection state: $message")

    /**
     * 이벤트 전송에 실패했을 때 발생하는 예외
     */
    class EventSendFailedException(eventType: SseEventType, cause: Throwable) : SseException("Failed to send SSE event: $eventType", cause)

    /**
     * 연결 수 제한을 초과했을 때 발생하는 예외
     */
    class ConnectionLimitExceededException(limit: Int) : SseException("SSE connection limit exceeded: $limit")

    /**
     * 권한이 없는 연결 시도일 때 발생하는 예외
     */
    class UnauthorizedConnectionException(message: String) : SseException("Unauthorized SSE connection: $message")

    /**
     * 연결을 찾을 수 없을 때 발생하는 예외
     */
    class ConnectionNotFoundException(connectionId: String) : SseException("SSE connection not found: $connectionId")
}
