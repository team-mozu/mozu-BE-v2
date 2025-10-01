package team.mozu.dsm.domain.sse.model

import java.time.LocalDateTime

/**
 * 재연결 가이드 정보
 */
data class ReconnectionGuide(
    val connectionId: String,
    val shouldReconnect: Boolean,
    val recommendedDelay: Long, // 밀리초
    val maxRetryAttempts: Int,
    val lastEventTime: LocalDateTime?,
    val missedEventCount: Int,
    val reconnectionUrl: String?,
    val additionalInfo: Map<String, Any> = emptyMap()
)
