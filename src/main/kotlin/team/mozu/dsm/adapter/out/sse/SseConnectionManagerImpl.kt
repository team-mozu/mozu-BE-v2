package team.mozu.dsm.adapter.out.sse

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.application.port.out.sse.EventManager
import team.mozu.dsm.application.port.out.sse.SseConnectionManager
import team.mozu.dsm.domain.sse.exception.SseException
import team.mozu.dsm.domain.sse.model.ConnectionStats
import team.mozu.dsm.domain.sse.model.ConnectionStatus
import team.mozu.dsm.domain.sse.model.ConnectionType
import team.mozu.dsm.domain.sse.model.ReconnectionGuide
import team.mozu.dsm.domain.sse.model.SseConnection
import team.mozu.dsm.domain.sse.model.SseEvent
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class SseConnectionManagerImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
    private val circuitBreaker: SseCircuitBreaker,
    private val retryHandler: SseRetryHandler,
    private val applicationContext: org.springframework.context.ApplicationContext
) : SseConnectionManager {

    // Lazy initialization to avoid circular dependency
    private val reconnectionManager: SseReconnectionManager by lazy {
        applicationContext.getBean(SseReconnectionManager::class.java)
    }

    private val logger = LoggerFactory.getLogger(SseConnectionManagerImpl::class.java)

    // 메모리에 실제 SseEmitter 객체들을 저장 (Redis에는 직렬화 불가)
    private val activeEmitters = ConcurrentHashMap<String, SseEmitter>()

    // 연결별 마지막 처리된 이벤트 ID 추적 (중복 방지용)
    private val lastProcessedEventIds = ConcurrentHashMap<String, String>()

    override fun addTeamConnection(teamId: UUID, emitter: SseEmitter): String {
        val connectionId = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        try {
            // 메모리에 emitter 저장
            activeEmitters[connectionId] = emitter

            // Redis에 메타데이터 저장
            val metadata = SseConnectionMetadata(
                id = connectionId,
                teamId = teamId,
                connectionType = ConnectionType.TEAM_STUDENT,
                connectedAt = now,
                lastActivity = now,
                isActive = true
            )

            redisTemplate.opsForValue().set(
                SseRedisKeys.connectionMetadataKey(connectionId),
                metadata
            )

            // 팀별 연결 목록에 추가
            redisTemplate.opsForSet().add(
                SseRedisKeys.teamConnectionsKey(teamId),
                connectionId
            )

            // emitter 완료/에러 시 정리 콜백 설정
            emitter.onCompletion {
                logger.debug("SSE connection completed: connectionId={}", connectionId)
                removeTeamConnection(connectionId)
            }
            emitter.onError { throwable ->
                logger.warn("SSE connection error: connectionId={}, error={}", connectionId, throwable?.message)
                handleConnectionError(connectionId, throwable ?: RuntimeException("Unknown SSE error"))
            }
            emitter.onTimeout {
                logger.warn("SSE connection timeout: connectionId={}", connectionId)
                handleConnectionError(connectionId, SseException.ConnectionClosedException("Connection timeout"))
            }

            logger.info("Team SSE connection added: connectionId={}, teamId={}", connectionId, teamId)
            return connectionId
        } catch (e: Exception) {
            activeEmitters.remove(connectionId)
            logger.error("Failed to add team SSE connection: teamId={}", teamId, e)
            throw SseException.InvalidConnectionStateException("Failed to add team connection: ${e.message}")
        }
    }

    override fun removeTeamConnection(connectionId: String) {
        try {
            // 메모리에서 emitter 제거
            activeEmitters.remove(connectionId)

            // Redis에서 메타데이터 조회 후 제거
            val metadataKey = SseRedisKeys.connectionMetadataKey(connectionId)
            val metadata = redisTemplate.opsForValue().get(metadataKey) as? SseConnectionMetadata

            if (metadata != null) {
                // 팀별 연결 목록에서 제거
                metadata.teamId?.let { teamId ->
                    redisTemplate.opsForSet().remove(
                        SseRedisKeys.teamConnectionsKey(teamId),
                        connectionId
                    )
                }

                // 메타데이터 제거
                redisTemplate.delete(metadataKey)

                logger.info("Team SSE connection removed: connectionId={}, teamId={}", connectionId, metadata.teamId)
            }
        } catch (e: Exception) {
            logger.error("Failed to remove team SSE connection: connectionId={}", connectionId, e)
        }
    }

    override fun getTeamConnections(teamId: UUID): List<SseConnection> {
        return try {
            val connectionIds = redisTemplate.opsForSet().members(
                SseRedisKeys.teamConnectionsKey(teamId)
            ) as? Set<String> ?: emptySet()

            connectionIds.mapNotNull { connectionId ->
                getConnectionById(connectionId)
            }
        } catch (e: Exception) {
            logger.error("Failed to get team connections: teamId={}", teamId, e)
            emptyList()
        }
    }

    override fun getAllTeamConnections(): List<SseConnection> {
        return try {
            val pattern = "${SseRedisKeys.TEAM_CONNECTIONS}:*"
            val keys = redisTemplate.keys(pattern)

            keys.flatMap { key ->
                val connectionIds = redisTemplate.opsForSet().members(key) as? Set<String> ?: emptySet()
                connectionIds.mapNotNull { connectionId ->
                    getConnectionById(connectionId)
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to get all team connections", e)
            emptyList()
        }
    }

    override fun addAdminConnection(lessonId: UUID, emitter: SseEmitter): String {
        val connectionId = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        try {
            // 메모리에 emitter 저장
            activeEmitters[connectionId] = emitter

            // Redis에 메타데이터 저장
            val metadata = SseConnectionMetadata(
                id = connectionId,
                lessonId = lessonId,
                connectionType = ConnectionType.ADMIN,
                connectedAt = now,
                lastActivity = now,
                isActive = true
            )

            redisTemplate.opsForValue().set(
                SseRedisKeys.connectionMetadataKey(connectionId),
                metadata
            )

            // 수업별 관리자 연결 목록에 추가
            redisTemplate.opsForSet().add(
                SseRedisKeys.adminConnectionsKey(lessonId),
                connectionId
            )

            // emitter 완료/에러 시 정리 콜백 설정
            emitter.onCompletion {
                logger.debug("Admin SSE connection completed: connectionId={}", connectionId)
                removeAdminConnection(connectionId)
            }
            emitter.onError { throwable ->
                logger.warn("Admin SSE connection error: connectionId={}, error={}", connectionId, throwable?.message)
                handleConnectionError(connectionId, throwable ?: RuntimeException("Unknown SSE error"))
            }
            emitter.onTimeout {
                logger.warn("Admin SSE connection timeout: connectionId={}", connectionId)
                handleConnectionError(connectionId, SseException.ConnectionClosedException("Connection timeout"))
            }

            logger.info("Admin SSE connection added: connectionId={}, lessonId={}", connectionId, lessonId)
            return connectionId
        } catch (e: Exception) {
            activeEmitters.remove(connectionId)
            logger.error("Failed to add admin SSE connection: lessonId={}", lessonId, e)
            throw SseException.InvalidConnectionStateException("Failed to add admin connection: ${e.message}")
        }
    }

    override fun removeAdminConnection(connectionId: String) {
        try {
            // 메모리에서 emitter 제거
            activeEmitters.remove(connectionId)

            // Redis에서 메타데이터 조회 후 제거
            val metadataKey = SseRedisKeys.connectionMetadataKey(connectionId)
            val metadata = redisTemplate.opsForValue().get(metadataKey) as? SseConnectionMetadata

            if (metadata != null) {
                // 수업별 관리자 연결 목록에서 제거
                metadata.lessonId?.let { lessonId ->
                    redisTemplate.opsForSet().remove(
                        SseRedisKeys.adminConnectionsKey(lessonId),
                        connectionId
                    )
                }

                // 메타데이터 제거
                redisTemplate.delete(metadataKey)

                logger.info("Admin SSE connection removed: connectionId={}, lessonId={}", connectionId, metadata.lessonId)
            }
        } catch (e: Exception) {
            logger.error("Failed to remove admin SSE connection: connectionId={}", connectionId, e)
        }
    }

    override fun getAdminConnections(lessonId: UUID): List<SseConnection> {
        return try {
            val connectionIds = redisTemplate.opsForSet().members(
                SseRedisKeys.adminConnectionsKey(lessonId)
            ) as? Set<String> ?: emptySet()

            connectionIds.mapNotNull { connectionId ->
                getConnectionById(connectionId)
            }
        } catch (e: Exception) {
            logger.error("Failed to get admin connections: lessonId={}", lessonId, e)
            emptyList()
        }
    }

    override fun sendToTeam(teamId: UUID, event: SseEvent) {
        val connections = getTeamConnections(teamId)
        val failedConnections = mutableListOf<String>()

        connections.forEach { connection ->
            val result = circuitBreaker.execute(connection.id) {
                retryHandler.executeWithRetry(
                    operation = {
                        sendEventSafely(connection.id, event)

                        logger.debug(
                            "Event sent to team: teamId={}, connectionId={}, eventType={}",
                            teamId,
                            connection.id,
                            event.type
                        )
                    }
                )
            }

            when (result) {
                is CircuitBreakerResult.Success -> {
                    when (val retryResult = result.value) {
                        is RetryResult.Success -> {
                            if (retryResult.attempts > 1) {
                                logger.info(
                                    "Event sent to team after {} attempts: teamId={}, connectionId={}, eventType={}",
                                    retryResult.attempts,
                                    teamId,
                                    connection.id,
                                    event.type
                                )
                            }
                        }
                        is RetryResult.Failed -> {
                            logger.error(
                                "Failed to send event to team after {} attempts: teamId={}, connectionId={}, eventType={}, reason={}",
                                retryResult.attempts,
                                teamId,
                                connection.id,
                                event.type,
                                retryResult.reason,
                                retryResult.exception
                            )
                            failedConnections.add(connection.id)
                        }
                    }
                }
                is CircuitBreakerResult.Failed -> {
                    logger.error(
                        "Circuit breaker failed for team connection: teamId={}, connectionId={}, eventType={}",
                        teamId,
                        connection.id,
                        event.type,
                        result.exception
                    )
                    failedConnections.add(connection.id)
                }
                is CircuitBreakerResult.Rejected -> {
                    logger.warn(
                        "Circuit breaker rejected team connection: teamId={}, connectionId={}, eventType={}, reason={}",
                        teamId,
                        connection.id,
                        event.type,
                        result.reason
                    )
                }
            }
        }

        // 실패한 연결들 정리
        failedConnections.forEach { connectionId ->
            handleConnectionError(connectionId, SseException.EventSendFailedException(event.type, RuntimeException("Event send failed")))
        }
    }

    override fun sendToAllTeams(lessonId: UUID, event: SseEvent) {
        try {
            val allTeamConnections = getAllTeamConnections()
            val lessonTeamConnections = allTeamConnections.filter { it.lessonId == lessonId }

            val failedConnections = mutableListOf<String>()
            var successCount = 0

            lessonTeamConnections.forEach { connection ->
                val result = circuitBreaker.execute(connection.id) {
                    retryHandler.executeWithRetry(
                        operation = {
                            val emitter = activeEmitters[connection.id]
                                ?: throw SseException.ConnectionNotFoundException(connection.id)

                            val eventData = SseEmitter.event()
                                .id(event.id)
                                .name(event.type.name)
                                .data(objectMapper.writeValueAsString(event))

                            emitter.send(eventData)
                            updateLastActivity(connection.id)

                            logger.debug(
                                "Broadcast event sent to team: lessonId={}, connectionId={}, eventType={}",
                                lessonId,
                                connection.id,
                                event.type
                            )
                        },
                        context = RetryContext(maxAttempts = 2)
                    )
                }

                when (result) {
                    is CircuitBreakerResult.Success -> {
                        when (val retryResult = result.value) {
                            is RetryResult.Success -> {
                                successCount++
                                if (retryResult.attempts > 1) {
                                    logger.info(
                                        "Broadcast event sent after {} attempts: lessonId={}, connectionId={}, eventType={}",
                                        retryResult.attempts,
                                        lessonId,
                                        connection.id,
                                        event.type
                                    )
                                }
                            }
                            is RetryResult.Failed -> {
                                logger.error(
                                    "Failed to send broadcast event after {} attempts: lessonId={}, connectionId={}, eventType={}, reason={}",
                                    retryResult.attempts,
                                    lessonId,
                                    connection.id,
                                    event.type,
                                    retryResult.reason,
                                    retryResult.exception
                                )
                                failedConnections.add(connection.id)
                            }
                        }
                    }
                    is CircuitBreakerResult.Failed -> {
                        logger.error(
                            "Circuit breaker failed for broadcast: lessonId={}, connectionId={}, eventType={}",
                            lessonId,
                            connection.id,
                            event.type,
                            result.exception
                        )
                        failedConnections.add(connection.id)
                    }
                    is CircuitBreakerResult.Rejected -> {
                        logger.warn(
                            "Circuit breaker rejected broadcast: lessonId={}, connectionId={}, eventType={}, reason={}",
                            lessonId,
                            connection.id,
                            event.type,
                            result.reason
                        )
                    }
                }
            }

            // 실패한 연결들 정리
            failedConnections.forEach { connectionId ->
                handleConnectionError(connectionId, SseException.EventSendFailedException(event.type, RuntimeException("Broadcast failed")))
            }

            logger.info(
                "Broadcast event completed: {}/{} teams successful for lesson: lessonId={}, eventType={}",
                successCount,
                lessonTeamConnections.size,
                lessonId,
                event.type
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to send broadcast event to all teams: lessonId={}, eventType={}",
                lessonId,
                event.type,
                e
            )
            throw SseException.EventSendFailedException(event.type, e)
        }
    }

    override fun sendToAdmin(lessonId: UUID, event: SseEvent) {
        val connections = getAdminConnections(lessonId)
        val failedConnections = mutableListOf<String>()

        connections.forEach { connection ->
            val result = circuitBreaker.execute(connection.id) {
                retryHandler.executeWithRetry(
                    operation = {
                        val emitter = activeEmitters[connection.id]
                            ?: throw SseException.ConnectionNotFoundException(connection.id)

                        val eventData = SseEmitter.event()
                            .id(event.id)
                            .name(event.type.name)
                            .data(objectMapper.writeValueAsString(event))

                        emitter.send(eventData)
                        updateLastActivity(connection.id)

                        logger.debug(
                            "Event sent to admin: lessonId={}, connectionId={}, eventType={}",
                            lessonId,
                            connection.id,
                            event.type
                        )
                    }
                )
            }

            when (result) {
                is CircuitBreakerResult.Success -> {
                    when (val retryResult = result.value) {
                        is RetryResult.Success -> {
                            if (retryResult.attempts > 1) {
                                logger.info(
                                    "Event sent to admin after {} attempts: lessonId={}, connectionId={}, eventType={}",
                                    retryResult.attempts,
                                    lessonId,
                                    connection.id,
                                    event.type
                                )
                            }
                        }
                        is RetryResult.Failed -> {
                            logger.error(
                                "Failed to send event to admin after {} attempts: lessonId={}, connectionId={}, eventType={}, reason={}",
                                retryResult.attempts,
                                lessonId,
                                connection.id,
                                event.type,
                                retryResult.reason,
                                retryResult.exception
                            )
                            failedConnections.add(connection.id)
                        }
                    }
                }
                is CircuitBreakerResult.Failed -> {
                    logger.error(
                        "Circuit breaker failed for admin connection: lessonId={}, connectionId={}, eventType={}",
                        lessonId,
                        connection.id,
                        event.type,
                        result.exception
                    )
                    failedConnections.add(connection.id)
                }
                is CircuitBreakerResult.Rejected -> {
                    logger.warn(
                        "Circuit breaker rejected admin connection: lessonId={}, connectionId={}, eventType={}, reason={}",
                        lessonId,
                        connection.id,
                        event.type,
                        result.reason
                    )
                }
            }
        }

        // 실패한 연결들 정리
        failedConnections.forEach { connectionId ->
            handleConnectionError(connectionId, SseException.EventSendFailedException(event.type, RuntimeException("Admin event send failed")))
        }
    }

    override fun cleanupDeadConnections() {
        try {
            logger.debug("Starting dead connections cleanup")
            val cleanedCount = detectAndCleanupAbnormalConnections()
            logger.info("Dead connections cleanup completed: {} connections cleaned", cleanedCount)
        } catch (e: Exception) {
            logger.error("Failed to cleanup dead connections", e)
        }
    }

    override fun getConnectionStats(): ConnectionStats {
        return try {
            val teamKeys = redisTemplate.keys("${SseRedisKeys.TEAM_CONNECTIONS}:*")
            val adminKeys = redisTemplate.keys("${SseRedisKeys.ADMIN_CONNECTIONS}:*")

            var teamConnections = 0
            var adminConnections = 0

            teamKeys.forEach { key ->
                val size = redisTemplate.opsForSet().size(key) ?: 0
                teamConnections += size.toInt()
            }

            adminKeys.forEach { key ->
                val size = redisTemplate.opsForSet().size(key) ?: 0
                adminConnections += size.toInt()
            }

            val totalConnections = teamConnections + adminConnections
            val activeConnections = activeEmitters.size
            val deadConnections = totalConnections - activeConnections

            ConnectionStats(
                totalConnections = totalConnections,
                teamConnections = teamConnections,
                adminConnections = adminConnections,
                activeConnections = activeConnections,
                deadConnections = deadConnections
            )
        } catch (e: Exception) {
            logger.error("Failed to get connection stats", e)
            ConnectionStats(0, 0, 0, 0, 0)
        }
    }

    override fun updateLastActivity(connectionId: String) {
        try {
            val metadataKey = SseRedisKeys.connectionMetadataKey(connectionId)
            val metadata = redisTemplate.opsForValue().get(metadataKey) as? SseConnectionMetadata

            if (metadata != null) {
                val updatedMetadata = metadata.copy(lastActivity = LocalDateTime.now())
                redisTemplate.opsForValue().set(metadataKey, updatedMetadata)
            }
        } catch (e: Exception) {
            logger.error("Failed to update last activity: connectionId={}", connectionId, e)
        }
    }

    override fun isConnectionActive(connectionId: String): Boolean {
        return try {
            activeEmitters.containsKey(connectionId) &&
                redisTemplate.hasKey(SseRedisKeys.connectionMetadataKey(connectionId))
        } catch (e: Exception) {
            logger.error("Failed to check connection active status: connectionId={}", connectionId, e)
            false
        }
    }

    override fun performHealthCheck(connectionId: String): Boolean {
        return try {
            val emitter = activeEmitters[connectionId]
            if (emitter == null) {
                logger.debug("Health check failed - emitter not found: connectionId={}", connectionId)
                return false
            }

            // 헬스체크 이벤트 전송 시도
            val healthCheckEvent = SseEmitter.event()
                .id("health-${System.currentTimeMillis()}")
                .name("HEARTBEAT")
                .data("ping")

            emitter.send(healthCheckEvent)

            // Redis에 헬스체크 시간 기록
            redisTemplate.opsForValue().set(
                SseRedisKeys.healthCheckKey(connectionId),
                LocalDateTime.now(),
                java.time.Duration.ofMinutes(5)
            )

            updateLastActivity(connectionId)

            logger.debug("Health check successful: connectionId={}", connectionId)
            true
        } catch (e: Exception) {
            logger.warn("Health check failed: connectionId={}", connectionId, e)
            handleConnectionError(connectionId, e)
            false
        }
    }

    override fun performHealthCheckAll(): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()

        try {
            activeEmitters.keys.forEach { connectionId ->
                results[connectionId] = performHealthCheck(connectionId)
            }

            val successCount = results.values.count { it }
            val totalCount = results.size

            logger.info("Health check completed: {}/{} connections healthy", successCount, totalCount)
        } catch (e: Exception) {
            logger.error("Failed to perform health check on all connections", e)
        }

        return results
    }

    override fun handleConnectionError(connectionId: String, error: Throwable) {
        try {
            logger.warn("Handling connection error: connectionId={}, error={}", connectionId, error.message)

            val metadata = redisTemplate.opsForValue().get(
                SseRedisKeys.connectionMetadataKey(connectionId)
            ) as? SseConnectionMetadata

            if (metadata != null) {
                // 연결 타입에 따라 적절한 정리 메서드 호출
                when (metadata.connectionType) {
                    ConnectionType.TEAM_STUDENT -> removeTeamConnection(connectionId)
                    ConnectionType.ADMIN -> removeAdminConnection(connectionId)
                }

                logger.info(
                    "Connection cleaned up due to error: connectionId={}, type={}",
                    connectionId,
                    metadata.connectionType
                )
            } else {
                // 메타데이터가 없는 경우 메모리에서만 제거
                activeEmitters.remove(connectionId)
                logger.info("Orphaned connection removed from memory: connectionId={}", connectionId)
            }
        } catch (e: Exception) {
            logger.error("Failed to handle connection error: connectionId={}", connectionId, e)
        }
    }

    override fun detectAndCleanupAbnormalConnections(): Int {
        var cleanedCount = 0

        try {
            val now = LocalDateTime.now()
            val staleThreshold = now.minusMinutes(10) // 10분 이상 비활성
            val deadThreshold = now.minusMinutes(30) // 30분 이상 완전 비활성

            // 메모리의 모든 연결 검사
            val connectionsToCheck = activeEmitters.keys.toList()

            connectionsToCheck.forEach { connectionId ->
                try {
                    val metadata = redisTemplate.opsForValue().get(
                        SseRedisKeys.connectionMetadataKey(connectionId)
                    ) as? SseConnectionMetadata

                    val shouldCleanup = when {
                        // 메타데이터가 없는 경우
                        metadata == null -> {
                            logger.debug("Cleaning up connection with missing metadata: {}", connectionId)
                            true
                        }
                        // 너무 오래 비활성인 경우
                        metadata.lastActivity.isBefore(deadThreshold) -> {
                            logger.debug(
                                "Cleaning up dead connection: {} (last activity: {})",
                                connectionId,
                                metadata.lastActivity
                            )
                            true
                        }
                        // 헬스체크 실패한 경우
                        metadata.lastActivity.isBefore(staleThreshold) && !performHealthCheck(connectionId) -> {
                            logger.debug("Cleaning up stale connection that failed health check: {}", connectionId)
                            true
                        }
                        else -> false
                    }

                    if (shouldCleanup) {
                        handleConnectionError(
                            connectionId,
                            SseException.ConnectionClosedException("Abnormal connection detected")
                        )
                        cleanedCount++
                    }
                } catch (e: Exception) {
                    logger.error("Error while checking connection: connectionId={}", connectionId, e)
                    handleConnectionError(connectionId, e)
                    cleanedCount++
                }
            }

            logger.info("Abnormal connections cleanup completed: {} connections cleaned", cleanedCount)
        } catch (e: Exception) {
            logger.error("Failed to detect and cleanup abnormal connections", e)
        }

        return cleanedCount
    }

    override fun recoverMissedEvents(connectionId: String, lastEventTime: LocalDateTime): Int {
        return try {
            val metadata = redisTemplate.opsForValue().get(
                SseRedisKeys.connectionMetadataKey(connectionId)
            ) as? SseConnectionMetadata

            if (metadata?.lessonId == null) {
                logger.debug("Cannot recover events - no lesson ID for connection: {}", connectionId)
                return 0
            }

            val emitter = activeEmitters[connectionId]
            if (emitter == null) {
                logger.debug("Cannot recover events - emitter not found for connection: {}", connectionId)
                return 0
            }

            // EventManager를 통해 놓친 이벤트 조회
            val eventManager = applicationContext.getBean(EventManager::class.java)
            val missedEvents = eventManager.getEventHistory(metadata.lessonId, lastEventTime)

            var recoveredCount = 0
            val duplicateEventIds = mutableSetOf<String>()

            missedEvents.forEach { event ->
                try {
                    // 중복 이벤트 방지
                    if (!duplicateEventIds.contains(event.id)) {
                        val eventData = SseEmitter.event()
                            .id(event.id)
                            .name("RECOVERY_${event.type.name}")
                            .data(objectMapper.writeValueAsString(event))

                        emitter.send(eventData)
                        duplicateEventIds.add(event.id)
                        recoveredCount++

                        logger.debug(
                            "Recovered event for connection: connectionId={}, eventId={}, eventType={}",
                            connectionId,
                            event.id,
                            event.type
                        )
                    }
                } catch (e: Exception) {
                    logger.error(
                        "Failed to recover event: connectionId={}, eventId={}, eventType={}",
                        connectionId,
                        event.id,
                        event.type,
                        e
                    )
                }
            }

            if (recoveredCount > 0) {
                logger.info(
                    "Event recovery completed: connectionId={}, recovered={}, duplicatesSkipped={}",
                    connectionId,
                    recoveredCount,
                    missedEvents.size - recoveredCount
                )

                // 복구 완료 알림 전송
                sendConnectionStatusFeedback(connectionId, ConnectionStatus.CONNECTED)
            }

            recoveredCount
        } catch (e: Exception) {
            logger.error("Failed to recover missed events: connectionId={}", connectionId, e)
            0
        }
    }

    override fun sendConnectionStatusFeedback(connectionId: String, status: ConnectionStatus) {
        try {
            val emitter = activeEmitters[connectionId]
            if (emitter == null) {
                logger.debug("Cannot send status feedback - emitter not found: {}", connectionId)
                return
            }

            val statusEvent = SseEmitter.event()
                .id("status-${System.currentTimeMillis()}")
                .name("CONNECTION_STATUS")
                .data(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "connectionId" to connectionId,
                            "status" to status.name,
                            "timestamp" to LocalDateTime.now()
                        )
                    )
                )

            emitter.send(statusEvent)
            updateLastActivity(connectionId)

            logger.debug("Connection status feedback sent: connectionId={}, status={}", connectionId, status)
        } catch (e: Exception) {
            logger.error(
                "Failed to send connection status feedback: connectionId={}, status={}",
                connectionId,
                status,
                e
            )
        }
    }

    override fun getReconnectionGuide(connectionId: String): ReconnectionGuide {
        return try {
            val metadata = redisTemplate.opsForValue().get(
                SseRedisKeys.connectionMetadataKey(connectionId)
            ) as? SseConnectionMetadata

            val isActive = isConnectionActive(connectionId)
            val circuitBreakerStatus = circuitBreaker.getConnectionStatus(connectionId)

            val shouldReconnect = when {
                isActive -> false // 이미 활성 상태
                circuitBreakerStatus == CircuitStatus.OPEN -> false // Circuit Breaker가 열려있음
                metadata == null -> false // 메타데이터 없음
                else -> true
            }

            val recommendedDelay = when (circuitBreakerStatus) {
                CircuitStatus.OPEN -> 60000L // 1분
                CircuitStatus.HALF_OPEN -> 30000L // 30초
                else -> 5000L // 5초
            }

            val missedEventCount = if (metadata?.lessonId != null) {
                val eventManager = applicationContext.getBean(EventManager::class.java)
                val lastEventTime = metadata.lastActivity.minusMinutes(5) // 5분 전부터 확인
                eventManager.getEventHistory(metadata.lessonId, lastEventTime).size
            } else {
                0
            }

            val reconnectionUrl = when (metadata?.connectionType) {
                ConnectionType.TEAM_STUDENT -> metadata.teamId?.let { "/team/sse?teamId=$it" }
                ConnectionType.ADMIN -> metadata.lessonId?.let { "/admin/sse?lessonId=$it" }
                else -> null
            }

            ReconnectionGuide(
                connectionId = connectionId,
                shouldReconnect = shouldReconnect,
                recommendedDelay = recommendedDelay,
                maxRetryAttempts = 3,
                lastEventTime = metadata?.lastActivity,
                missedEventCount = missedEventCount,
                reconnectionUrl = reconnectionUrl,
                additionalInfo = mapOf(
                    "circuitBreakerStatus" to circuitBreakerStatus.name,
                    "connectionType" to (metadata?.connectionType?.name ?: "UNKNOWN"),
                    "isActive" to isActive
                )
            )
        } catch (e: Exception) {
            logger.error("Failed to get reconnection guide: connectionId={}", connectionId, e)
            ReconnectionGuide(
                connectionId = connectionId,
                shouldReconnect = false,
                recommendedDelay = 30000L,
                maxRetryAttempts = 3,
                lastEventTime = null,
                missedEventCount = 0,
                reconnectionUrl = null,
                additionalInfo = mapOf("error" to (e.message ?: "Unknown error"))
            )
        }
    }

    /**
     * 이벤트를 안전하게 전송하는 헬퍼 메서드 (중복 방지 포함)
     */
    private fun sendEventSafely(connectionId: String, event: SseEvent, eventNamePrefix: String = ""): Boolean {
        return try {
            val emitter = activeEmitters[connectionId]
                ?: throw SseException.ConnectionNotFoundException(connectionId)

            // 중복 이벤트 방지 확인
            val lastEventId = lastProcessedEventIds[connectionId]
            if (lastEventId == event.id) {
                logger.debug("Skipping duplicate event: connectionId={}, eventId={}", connectionId, event.id)
                return true
            }

            val eventName = if (eventNamePrefix.isNotEmpty()) {
                "${eventNamePrefix}_${event.type.name}"
            } else {
                event.type.name
            }

            val eventData = SseEmitter.event()
                .id(event.id)
                .name(eventName)
                .data(objectMapper.writeValueAsString(event))

            emitter.send(eventData)
            updateLastActivity(connectionId)

            // 마지막 처리된 이벤트 ID 업데이트
            lastProcessedEventIds[connectionId] = event.id

            true
        } catch (e: Exception) {
            logger.error("Failed to send event safely: connectionId={}, eventId={}", connectionId, event.id, e)
            throw e
        }
    }

    private fun getConnectionById(connectionId: String): SseConnection? {
        return try {
            val metadata = redisTemplate.opsForValue().get(
                SseRedisKeys.connectionMetadataKey(connectionId)
            ) as? SseConnectionMetadata

            val emitter = activeEmitters[connectionId]

            if (metadata != null && emitter != null) {
                SseConnection(
                    id = metadata.id,
                    emitter = emitter,
                    teamId = metadata.teamId,
                    lessonId = metadata.lessonId,
                    connectionType = metadata.connectionType,
                    connectedAt = metadata.connectedAt,
                    lastActivity = metadata.lastActivity,
                    isActive = metadata.isActive
                )
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to get connection by ID: connectionId={}", connectionId, e)
            null
        }
    }
}
