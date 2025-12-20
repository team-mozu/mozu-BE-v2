package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.lesson.dto.NextLessonEventDTO
import team.mozu.dsm.application.exception.lesson.CannotNextLessonException
import team.mozu.dsm.application.port.`in`.lesson.NextLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class NextLessonService(
    private val lessonFacade: LessonFacade,
    private val lessonPort: CommandLessonPort,
    private val publishSsePort: PublishSsePort,
    private val teamPort: QueryTeamPort,
    private val securityPort: SecurityPort
) : NextLessonUseCase {

    companion object {
        private const val NEXT_LESSON_EVENT = "CLASS_NEXT_INV_START"
    }

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun next(lessonId: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val lesson = lessonFacade.findByLessonId(lessonId)

        if (lesson.organId != organ.id) {
            throw CannotNextLessonException
        }

        // 다음 차수 진행을 위해 curInvRound 업데이트
        val updatedLesson = lessonPort.updateCurInvRound(lesson.id!!)
        // 해당 수업에 참여중인 팀 조회
        val teams = teamPort.findAllByLessonNum(lesson.lessonNum!!)

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
                            publishSsePort.publishTo("team-sse:${team.id}", NEXT_LESSON_EVENT, eventData)
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
