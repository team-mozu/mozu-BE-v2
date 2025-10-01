package team.mozu.dsm.adapter.out.sse

import java.util.UUID

/**
 * SSE 관련 Redis 키 생성 유틸리티
 */
object SseRedisKeys {

    const val CONNECTION_METADATA = "sse:connection:metadata"
    const val TEAM_CONNECTIONS = "sse:team:connections"
    const val ADMIN_CONNECTIONS = "sse:admin:connections"
    const val HEALTH_CHECK = "sse:health:check"
    const val RECONNECTION_GUIDE = "sse:reconnection:guide"
    const val RECONNECTION_ATTEMPTS = "sse:reconnection:attempts"
    const val PROCESSED_EVENTS = "sse:processed:events"
    const val EVENT_HISTORY = "sse:event:history"

    fun connectionMetadataKey(connectionId: String): String {
        return "$CONNECTION_METADATA:$connectionId"
    }

    fun teamConnectionsKey(teamId: UUID): String {
        return "$TEAM_CONNECTIONS:$teamId"
    }

    fun adminConnectionsKey(lessonId: UUID): String {
        return "$ADMIN_CONNECTIONS:$lessonId"
    }

    fun healthCheckKey(connectionId: String): String {
        return "$HEALTH_CHECK:$connectionId"
    }

    fun reconnectionGuideKey(connectionId: String): String {
        return "$RECONNECTION_GUIDE:$connectionId"
    }

    fun reconnectionAttemptsKey(connectionId: String): String {
        return "$RECONNECTION_ATTEMPTS:$connectionId"
    }

    fun processedEventsKey(connectionId: String): String {
        return "$PROCESSED_EVENTS:$connectionId"
    }

    fun eventHistoryKey(lessonId: UUID): String {
        return "$EVENT_HISTORY:$lessonId"
    }
}
