package team.mozu.dsm.domain.sse.model

/**
 * SSE 연결 통계 정보를 나타내는 데이터 클래스
 */
data class ConnectionStats(
    val totalConnections: Int,
    val teamConnections: Int,
    val adminConnections: Int,
    val activeConnections: Int,
    val deadConnections: Int
)
