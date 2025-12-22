package team.mozu.dsm.application.service.team.facade

import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.lesson.dto.NextLessonEventDTO
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.domain.sse.model.SseEventType
import java.util.UUID

@Component
class TeamFacade(
    private val commandLessonPort: CommandLessonPort,
    private val publishSsePort: PublishSsePort,
    private val teamPort: QueryTeamPort
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    fun updateCurRoundAndPublishEvent(lessonId: UUID, eventType: SseEventType) {
        // 수업 시작 및 다음 차수 진행을 위해 curInvRound 업데이트
        val updatedLesson = commandLessonPort.updateCurInvRound(lessonId)
        // 해당 수업에 참여중인 팀 조회
        val teams = teamPort.findAllByLessonId(lessonId)

        // 트랜잭션 커밋 후 이벤트 발행
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    teams.forEach { team ->
                        try {
                            val eventData = NextLessonEventDTO(
                                lessonId = updatedLesson.id!!,
                                curInvRound = updatedLesson.curInvRound,
                                teamId = team.id!!,
                                teamName = team.teamName,
                                schoolName = team.schoolName
                            )
                            publishSsePort.publishTo("team-sse:${team.id}", eventType.eventName, eventData)
                        } catch (e: Exception) {
                            // 개별 팀 이벤트 전송 실패가 다른 팀에 영향을 주지 않도록 예외 처리
                            log.error("Failed to send CLASS_NEXT_INV_START event to team ${team.id}", e)
                        }
                    }
                }
            }
        )
    }
}
