package team.mozu.dsm.application.service.admin

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.out.sse.SseAuthenticationServiceImpl
import team.mozu.dsm.adapter.out.sse.SseRateLimitServiceImpl

import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.sse.EventManager
import team.mozu.dsm.application.port.out.sse.SseConnectionManager
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.domain.sse.exception.SseAuthenticationException
import team.mozu.dsm.domain.sse.exception.SseException
import team.mozu.dsm.domain.sse.exception.SseRateLimitException
import team.mozu.dsm.domain.sse.model.SseEventType
import team.mozu.dsm.domain.sse.model.event.AllFinishedEventData
import team.mozu.dsm.domain.sse.model.event.InvestmentRoundStartEventData
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminSseServiceImpl(
    private val sseConnectionManager: SseConnectionManager,
    private val eventManager: EventManager,
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val sseAuthenticationService: SseAuthenticationServiceImpl,
    private val sseRateLimitService: team.mozu.dsm.application.port.out.sse.SseRateLimitService
) {

    fun connectAdminSSEWithAuth(lessonId: UUID, authentication: Authentication): SseEmitter {
        // 1. 관리자 인증 및 권한 검증
        if (!sseAuthenticationService.validateAdminAuthentication(authentication, lessonId)) {
            throw SseAuthenticationException.InsufficientPermissionException("ADMIN_SSE", lessonId.toString())
        }

        // 2. Rate Limiting 검증
        val organCode = if (authentication.principal is team.mozu.dsm.global.security.auth.CustomUserDetails) {
            (authentication.principal as team.mozu.dsm.global.security.auth.CustomUserDetails).organ.organCode
        } else {
            "unknown"
        }

        val clientId = "admin:$organCode:$lessonId"

        // 2.1. 연결 시도 제한 확인
        if (!sseRateLimitService.allowConnection(clientId, "ADMIN_SSE")) {
            throw SseRateLimitException.ConnectionAttemptsExceededException(0, 0)
        }

        // 2.2. 관리자 연결 수 제한 확인
        val rateLimitServiceImpl = sseRateLimitService as SseRateLimitServiceImpl
        if (!rateLimitServiceImpl.validateAdminConnectionLimit(lessonId)) {
            val currentConnections = sseRateLimitService.getAdminConnectionCount(lessonId)
            throw SseRateLimitException.AdminConnectionLimitExceededException(lessonId, currentConnections, 5)
        }

        // 2.3. 연결 시도 기록
        sseRateLimitService.recordConnectionAttempt(clientId, "ADMIN_SSE")

        // 3. 기존 연결 로직 호출
        return connectAdminSSE(lessonId)
    }

    fun connectAdminSSE(lessonId: UUID): SseEmitter {
        // 수업 정보 검증
        val lesson = queryLessonPort.findById(lessonId)
            ?: throw LessonNotFoundException

        // SSE Emitter 생성 (관리자는 더 긴 타임아웃 설정)
        val emitter = SseEmitter(600_000L) // 10분 타임아웃

        try {
            // Connection Manager에 관리자 연결 등록
            val connectionId = sseConnectionManager.addAdminConnection(lessonId, emitter)

            // 연결 설정 완료 이벤트 전송
            val connectionEvent = eventManager.createEvent(
                type = SseEventType.CONNECTION_ESTABLISHED,
                data = mapOf(
                    "connectionId" to connectionId,
                    "lessonId" to lessonId,
                    "message" to "Admin SSE connection established successfully",
                    "role" to "admin"
                ),
                lessonId = lessonId
            )

            emitter.send(
                SseEmitter.event()
                    .id(connectionEvent.id)
                    .name(connectionEvent.type.name)
                    .data(connectionEvent)
            )

            // 연결 해제 시 정리 작업 설정
            emitter.onCompletion {
                handleAdminDisconnection(connectionId)
            }

            emitter.onTimeout {
                handleAdminDisconnection(connectionId)
            }

            emitter.onError {
                handleAdminDisconnection(connectionId)
            }

            return emitter
        } catch (e: Exception) {
            throw SseException.ConnectionClosedException("Failed to establish admin SSE connection for lesson: $lessonId")
        }
    }

    fun startNextInvestmentRound(lessonId: UUID, roundNumber: Int) {
        try {
            // 수업 정보 검증
            val lesson = queryLessonPort.findById(lessonId)
                ?: throw LessonNotFoundException

            // 투자 라운드 시작 이벤트 데이터 생성
            val roundStartData = InvestmentRoundStartEventData(
                lessonId = lessonId,
                roundNumber = roundNumber,
                startTime = LocalDateTime.now(),
                timeLimit = null // 필요시 설정 가능
            )

            val roundStartEvent = eventManager.createEvent(
                type = SseEventType.CLASS_NEXT_INV_START,
                data = roundStartData,
                lessonId = lessonId
            )

            // 모든 팀에게 브로드캐스트
            eventManager.publishBroadcastEvent(lessonId, roundStartEvent)

            // 이벤트 히스토리 저장
            eventManager.saveEventHistory(lessonId, roundStartEvent)

            // 브로드캐스트 후 실패한 연결들 정리
            sseConnectionManager.cleanupDeadConnections()
        } catch (e: Exception) {
            throw SseException.EventSendFailedException(SseEventType.CLASS_NEXT_INV_START, e)
        }
    }

    fun checkAllTeamsCompleted(lessonId: UUID): Boolean {
        try {
            // 해당 수업의 모든 팀 조회
            val teams = queryTeamPort.findAllByLessonId(lessonId)

            if (teams.isEmpty()) {
                return false
            }

            // 모든 팀이 투자를 완료했는지 확인
            // isInvestmentInProgress가 false인 팀들이 모든 팀인지 확인
            val completedTeams = teams.filter { !it.isInvestmentInProgress }

            return completedTeams.size == teams.size
        } catch (e: Exception) {
            // 예외 발생 시 false 반환 (안전한 기본값)
            return false
        }
    }

    fun notifyAllFinished(lessonId: UUID) {
        try {
            // 수업 정보 검증
            val lesson = queryLessonPort.findById(lessonId)
                ?: throw LessonNotFoundException

            // 팀 완료 상태 정보 수집
            val teams = queryTeamPort.findAllByLessonId(lessonId)
            val completedTeams = teams.filter { !it.isInvestmentInProgress }

            // 전체 완료 이벤트 데이터 생성
            val allFinishedData = AllFinishedEventData(
                lessonId = lessonId,
                completedTeams = completedTeams.size,
                totalTeams = teams.size,
                completedAt = LocalDateTime.now()
            )

            val allFinishedEvent = eventManager.createEvent(
                type = SseEventType.ALL_FINISHED,
                data = allFinishedData,
                lessonId = lessonId
            )

            // 관리자에게 전체 완료 알림 전송
            eventManager.publishAdminEvent(lessonId, allFinishedEvent)

            // 이벤트 히스토리 저장
            eventManager.saveEventHistory(lessonId, allFinishedEvent)
        } catch (e: Exception) {
            throw SseException.EventSendFailedException(SseEventType.ALL_FINISHED, e)
        }
    }

    fun handleAdminDisconnection(connectionId: String) {
        try {
            sseConnectionManager.removeAdminConnection(connectionId)
        } catch (e: Exception) {
            // 로깅만 하고 예외는 던지지 않음 (정리 작업이므로)
            println("Failed to cleanup admin connection: $connectionId, error: ${e.message}")
        }
    }

    fun validateAdminAccess(authentication: Authentication, lessonId: UUID): Boolean {
        return sseAuthenticationService.validateAdminAuthentication(authentication, lessonId)
    }
}
