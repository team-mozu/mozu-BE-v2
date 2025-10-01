package team.mozu.dsm.application.service.team

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.team.dto.TeamInvestmentCompletedEventDTO
import team.mozu.dsm.adapter.out.sse.SseClientIdentifier
import team.mozu.dsm.adapter.out.sse.SseRateLimitServiceImpl
import team.mozu.dsm.adapter.out.sse.SseTokenExtractor
import team.mozu.dsm.application.port.out.sse.SseAuthenticationService
import team.mozu.dsm.application.port.out.sse.SseRateLimitService
import team.mozu.dsm.domain.sse.exception.SseAuthenticationException
import team.mozu.dsm.domain.sse.exception.SseRateLimitException
import team.mozu.dsm.domain.sse.model.event.TeamParticipationEventData
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.TeamSseService
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.sse.EventManager
import team.mozu.dsm.application.port.out.sse.SseConnectionManager
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.domain.sse.exception.SseException
import team.mozu.dsm.domain.sse.model.SseEventType
import team.mozu.dsm.domain.sse.model.event.AllFinishedEventData
import team.mozu.dsm.domain.sse.model.event.TeamInvestmentCompletedEventData

import team.mozu.dsm.global.security.auth.StudentPrincipal
import java.time.LocalDateTime
import java.util.UUID

@Service
class TeamSseServiceImpl(
    private val sseConnectionManager: SseConnectionManager,
    private val eventManager: EventManager,
    private val queryLessonPort: QueryLessonPort,
    private val queryTeamPort: QueryTeamPort,
    private val reconnectionManager: team.mozu.dsm.adapter.out.sse.SseReconnectionManager,
    private val sseAuthenticationService: SseAuthenticationService,
    private val sseTokenExtractor: SseTokenExtractor,
    private val sseRateLimitService: SseRateLimitService,
    private val sseClientIdentifier: SseClientIdentifier
) : TeamSseService {

    override fun connectTeamSSEWithToken(
        teamId: UUID,
        request: HttpServletRequest,
        lastEventId: String?,
        isReconnection: Boolean
    ): SseEmitter {
        // 1. 토큰 추출
        val token = sseTokenExtractor.extractToken(request)
            ?: throw SseAuthenticationException.TokenMissingException()

        // 2. 토큰 검증 및 사용자 정보 추출
        val principal = sseAuthenticationService.validateToken(token)
            ?: throw SseAuthenticationException.InvalidTokenException()

        // 3. 토큰 만료 확인
        if (sseAuthenticationService.isTokenExpired(token)) {
            throw SseAuthenticationException.TokenExpiredException()
        }

        // 4. 팀 접근 권한 검증
        if (!sseAuthenticationService.validateTeamAccess(principal, teamId)) {
            throw SseAuthenticationException.InsufficientPermissionException("TEAM_SSE", teamId.toString())
        }

        // 5. Rate Limiting 검증
        val clientId = sseClientIdentifier.extractClientId(request, principal)

        // 5.1. 연결 시도 제한 확인
        if (!sseRateLimitService.allowConnection(clientId, "TEAM_SSE")) {
            throw SseRateLimitException.ConnectionAttemptsExceededException(0, 0) // 실제 값은 서비스에서 로깅됨
        }

        // 5.2. 팀별 연결 수 제한 확인
        val rateLimitServiceImpl = sseRateLimitService as SseRateLimitServiceImpl
        if (!rateLimitServiceImpl.validateTeamConnectionLimit(teamId)) {
            val currentConnections = sseRateLimitService.getTeamConnectionCount(teamId)
            throw SseRateLimitException.TeamConnectionLimitExceededException(teamId, currentConnections, 10)
        }

        // 5.3. 연결 시도 기록
        sseRateLimitService.recordConnectionAttempt(clientId, "TEAM_SSE")

        // 6. 기존 연결 로직 호출
        return connectTeamSSE(teamId, principal, lastEventId, isReconnection)
    }

    override fun connectTeamSSE(
        teamId: UUID,
        principal: StudentPrincipal,
        lastEventId: String?,
        isReconnection: Boolean
    ): SseEmitter {
        // 팀 정보 검증
        val team = try {
            queryTeamPort.findById(teamId)
        } catch (e: Exception) {
            throw TeamNotFoundException
        }

        // 권한 검증 - 해당 팀의 학생인지 확인
        if (principal.teamId != teamId) {
            sseAuthenticationService.logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Team ID mismatch"
            )
            throw SseException.InvalidConnectionStateException("Team access denied for student")
        }

        // 수업 정보 조회
        val lesson = queryLessonPort.findById(team.lessonId)
            ?: throw LessonNotFoundException

        // SSE Emitter 생성
        val emitter = SseEmitter(300_000L) // 5분 타임아웃

        try {
            // Connection Manager에 연결 등록
            val connectionId = sseConnectionManager.addTeamConnection(teamId, emitter)

            // 연결 설정 완료 이벤트 전송
            val connectionEvent = eventManager.createEvent(
                type = SseEventType.CONNECTION_ESTABLISHED,
                data = mapOf(
                    "connectionId" to connectionId,
                    "teamId" to teamId,
                    "message" to "SSE connection established successfully"
                ),
                teamId = teamId
            )

            emitter.send(
                SseEmitter.event()
                    .id(connectionEvent.id)
                    .name(connectionEvent.type.name)
                    .data(connectionEvent)
            )

            // 재연결인 경우 놓친 이벤트 복구
            if (isReconnection && lastEventId != null) {
                try {
                    val missedEvents = reconnectionManager.recoverMissedEvents(
                        connectionId,
                        lesson.id!!,
                        lastEventId
                    )

                    // 복구된 이벤트들을 클라이언트에게 전송
                    missedEvents.forEach { event ->
                        if (!reconnectionManager.isEventAlreadyProcessed(connectionId, event.id)) {
                            emitter.send(
                                SseEmitter.event()
                                    .id(event.id)
                                    .name(event.type.name)
                                    .data(event)
                            )
                        }
                    }

                    // 재연결 성공 시도 횟수 리셋
                    reconnectionManager.resetReconnectionAttempts(connectionId)

                    println("Recovered ${missedEvents.size} missed events for reconnection: teamId=$teamId")
                } catch (e: Exception) {
                    println("Failed to recover missed events during reconnection: ${e.message}")
                }
            }

            // 팀 참여 이벤트를 관리자에게 전송 (새 연결인 경우만)
            if (!isReconnection) {
                val participationEventData = TeamParticipationEventData(
                    teamId = team.id!!,
                    teamName = team.teamName,
                    schoolName = team.schoolName,
                    lessonNum = team.lessonNum,
                    participantCount = 1 // 새로 연결된 팀
                )

                val participationEvent = eventManager.createEvent(
                    type = SseEventType.TEAM_PART_IN,
                    data = participationEventData,
                    lessonId = lesson.id,
                    teamId = team.id
                )

                // 관리자에게 팀 참여 알림 전송
                try {
                    eventManager.publishAdminEvent(lesson.id!!, participationEvent)
                    eventManager.saveEventHistory(lesson.id, participationEvent)
                } catch (e: Exception) {
                    // 관리자 알림 실패해도 SSE 연결은 유지
                    println("Failed to notify admin about team participation: ${e.message}")
                }
            }

            // 연결 해제 시 정리 작업 설정
            emitter.onCompletion {
                handleTeamDisconnection(connectionId)
            }

            emitter.onTimeout {
                handleTeamDisconnection(connectionId)
            }

            emitter.onError {
                handleTeamDisconnection(connectionId)
            }

            return emitter
        } catch (e: Exception) {
            throw SseException.ConnectionClosedException("Failed to establish SSE connection for team: $teamId")
        }
    }

    override fun notifyInvestmentComplete(teamId: UUID, investmentData: TeamInvestmentCompletedEventDTO) {
        try {
            // 팀 정보 조회
            val team = try {
                queryTeamPort.findById(teamId)
            } catch (e: Exception) {
                throw TeamNotFoundException
            }

            val lesson = queryLessonPort.findById(team.lessonId)
                ?: throw LessonNotFoundException

            // 투자 완료 이벤트 데이터 생성
            val investmentCompletedData = TeamInvestmentCompletedEventData(
                teamId = investmentData.teamId,
                teamName = investmentData.teamName,
                curInvRound = investmentData.curInvRound,
                totalMoney = investmentData.totalMoney,
                valuationMoney = investmentData.valuationMoney,
                profitNum = investmentData.profitNum,
                completedAt = LocalDateTime.now()
            )

            val investmentEvent = eventManager.createEvent(
                type = SseEventType.TEAM_INV_END,
                data = investmentCompletedData,
                lessonId = lesson.id,
                teamId = teamId
            )

            // 팀에게 투자 완료 알림
            eventManager.publishTeamEvent(teamId, investmentEvent)

            // 관리자에게 투자 완료 알림
            eventManager.publishAdminEvent(lesson.id!!, investmentEvent)

            // 이벤트 히스토리 저장
            eventManager.saveEventHistory(lesson.id, investmentEvent)

            // 모든 팀이 완료되었는지 확인하고 관리자에게 알림
            checkAndNotifyAllTeamsCompleted(lesson.id)
        } catch (e: Exception) {
            throw SseException.EventSendFailedException(SseEventType.TEAM_INV_END, e)
        }
    }

    override fun handleTeamDisconnection(connectionId: String) {
        try {
            sseConnectionManager.removeTeamConnection(connectionId)
        } catch (e: Exception) {
            // 로깅만 하고 예외는 던지지 않음 (정리 작업이므로)
            println("Failed to cleanup team connection: $connectionId, error: ${e.message}")
        }
    }

    /**
     * 모든 팀의 투자 완료 상태를 확인하고, 모든 팀이 완료되었다면 관리자에게 알림을 전송합니다.
     */
    private fun checkAndNotifyAllTeamsCompleted(lessonId: UUID) {
        try {
            // 해당 수업의 모든 팀 조회
            val teams = queryTeamPort.findAllByLessonId(lessonId)

            if (teams.isEmpty()) {
                return
            }

            // 모든 팀이 투자를 완료했는지 확인
            val completedTeams = teams.filter { !it.isInvestmentInProgress }

            if (completedTeams.size == teams.size) {
                // 모든 팀이 완료되었으므로 관리자에게 알림
                notifyAllFinished(lessonId, completedTeams.size, teams.size)
            }
        } catch (e: Exception) {
            // 예외 발생 시 로깅만 하고 계속 진행
            println("Failed to check all teams completion status for lesson: $lessonId, error: ${e.message}")
        }
    }

    /**
     * 모든 팀이 완료되었을 때 관리자에게 알림을 전송합니다.
     */
    private fun notifyAllFinished(lessonId: UUID, completedTeams: Int, totalTeams: Int) {
        try {
            // 전체 완료 이벤트 데이터 생성
            val allFinishedData = AllFinishedEventData(
                lessonId = lessonId,
                completedTeams = completedTeams,
                totalTeams = totalTeams,
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
}
