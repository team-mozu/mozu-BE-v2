package team.mozu.dsm.application.service.team

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import team.mozu.dsm.adapter.`in`.sse.dto.SSEResponse
import team.mozu.dsm.adapter.out.sse.SsePersistenceAdapter
import team.mozu.dsm.adapter.out.sse.repository.SseEmitterRepository
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.`in`.team.ConnectTeamSSEUseCase
import team.mozu.dsm.application.port.out.sse.SubscribeSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import java.util.UUID

@Service
class ConnectTeamSSEService(
    private val subscribeSsePort: SubscribeSsePort,
    private val queryTeamPort: QueryTeamPort,
    private val sseEmitterRepository: SseEmitterRepository,
    private val ssePersistenceAdapter: SsePersistenceAdapter
) : ConnectTeamSSEUseCase {

    companion object {
        private const val CONNECTED_EVENT = "TEAM_SSE_CONNECTED"
        private val logger = LoggerFactory.getLogger(ConnectTeamSSEService::class.java)
    }

    override fun execute(teamId: UUID, lastEventId: String?): SseEmitter {
        logger.info("SSE 연결 시도 - TeamId: $teamId, LastEventId: $lastEventId")

        val team = queryTeamPort.findById(teamId)
        if (team == null) {
            logger.error("팀을 찾을 수 없음 - TeamId: $teamId")
            throw TeamNotFoundException
        }

        logger.info("팀 조회 성공 - Team: ${team.teamName}, LessonNum: ${team.lessonNum}")

        val clientId = "team-sse:${team.id}"

        return try {
            // Use recovery-enabled subscription if lastEventId is provided
            val emitter = if (lastEventId != null) {
                logger.info("이벤트 복구 모드로 SSE 연결 - ClientId: $clientId")
                ssePersistenceAdapter.subscribeWithRecovery(clientId, lastEventId, teamId)
            } else {
                logger.info("새 SSE 연결 - ClientId: $clientId")
                subscribeSsePort.subscribe(clientId)
            }

            try {
                emitter.send(
                    SseEmitter.event()
                        .name(CONNECTED_EVENT)
                        .data(
                            SSEResponse(
                                CONNECTED_EVENT,
                                "id ${team.id}번의 학생 클라이언트 SSE 연결되었습니다." + if (lastEventId != null) " (복구됨: $lastEventId)" else ""
                            )
                        )
                )
                logger.info("SSE 연결 성공 및 초기 이벤트 전송 완료 - TeamId: $teamId")
            } catch (e: Exception) {
                logger.error("SSE 초기 이벤트 전송 실패 - TeamId: $teamId", e)
                sseEmitterRepository.delete(clientId)
                emitter.completeWithError(e)
                throw e
            }

            emitter
        } catch (e: Exception) {
            logger.error("SSE 연결 생성 실패 - TeamId: $teamId", e)
            throw e
        }
    }
}
