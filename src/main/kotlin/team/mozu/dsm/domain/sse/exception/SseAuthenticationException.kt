package team.mozu.dsm.domain.sse.exception

/**
 * SSE 인증 관련 예외 클래스들
 */
sealed class SseAuthenticationException(message: String, cause: Throwable? = null) : SseException(message, cause) {

    /**
     * SSE 연결 시 토큰이 제공되지 않은 경우
     */
    class TokenMissingException : SseAuthenticationException("JWT token is required for SSE connection")

    /**
     * 제공된 토큰이 만료된 경우
     */
    class TokenExpiredException : SseAuthenticationException("JWT token has expired")

    /**
     * 제공된 토큰이 유효하지 않은 경우
     */
    class InvalidTokenException : SseAuthenticationException("Invalid JWT token provided")

    /**
     * 토큰은 유효하지만 요청한 리소스에 대한 권한이 없는 경우
     */
    class InsufficientPermissionException(resourceType: String, resourceId: String) : SseAuthenticationException("Insufficient permission to access $resourceType: $resourceId")

    /**
     * 잘못된 토큰 타입이 사용된 경우
     */
    class InvalidTokenTypeException(expectedType: String, actualType: String) : SseAuthenticationException("Invalid token type. Expected: $expectedType, Actual: $actualType")
}

/**
 * SSE Rate Limiting 관련 예외 클래스들
 */
sealed class SseRateLimitException(message: String, cause: Throwable? = null) : SseException(message, cause) {

    /**
     * 연결 시도 횟수 제한 초과
     */
    class ConnectionAttemptsExceededException(attempts: Int, limit: Int) : SseRateLimitException("Connection attempts exceeded: $attempts/$limit")

    /**
     * 클라이언트별 동시 연결 수 제한 초과
     */
    class ClientConnectionLimitExceededException(current: Int, limit: Int) : SseRateLimitException("Client connection limit exceeded: $current/$limit")

    /**
     * 팀별 연결 수 제한 초과
     */
    class TeamConnectionLimitExceededException(teamId: java.util.UUID, current: Int, limit: Int) : SseRateLimitException("Team connection limit exceeded for team $teamId: $current/$limit")

    /**
     * 관리자 연결 수 제한 초과
     */
    class AdminConnectionLimitExceededException(lessonId: java.util.UUID, current: Int, limit: Int) : SseRateLimitException("Admin connection limit exceeded for lesson $lessonId: $current/$limit")

    /**
     * 전체 시스템 연결 수 제한 초과
     */
    class SystemConnectionLimitExceededException(current: Int, limit: Int) : SseRateLimitException("System connection limit exceeded: $current/$limit")

    /**
     * 이벤트 전송 빈도 제한 초과
     */
    class EventSendRateLimitExceededException(sends: Int, limit: Int) : SseRateLimitException("Event send rate limit exceeded: $sends/$limit")
}
