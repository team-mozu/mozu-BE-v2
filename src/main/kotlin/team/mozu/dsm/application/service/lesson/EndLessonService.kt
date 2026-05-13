package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.out.sse.persistence.repository.SseEmitterRepository
import team.mozu.dsm.application.exception.lesson.CannotEndLessonException
import team.mozu.dsm.application.port.`in`.lesson.EndLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.team.CommandTeamPort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class EndLessonService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort,
    private val securityPort: SecurityPort,
    private val publishSsePort: PublishSsePort,
    private val queryTeamPort: QueryTeamPort,
    private val commandTeamPort: CommandTeamPort,
    private val sseEmitterRepository: SseEmitterRepository
) : EndLessonUseCase {

    companion object {
        private const val CLASS_CANCEL_EVENT = "CLASS_CANCEL"
    }

    @Transactional
    override fun execute(id: UUID) {
        val lesson = lessonFacade.findByLessonId(id)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotEndLessonException
        }

        // 해당 수업에 참여 중인 팀들 조회
        val teams = queryTeamPort.findAllByLessonId(lesson.id!!)

        commandLessonPort.updateIsInProgress(lesson.id)
        commandTeamPort.updateIsInvestmentInProgress(teams)

        // 트랜잭션 커밋 후 팀들에게 수업 취소 이벤트 발행
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    teams.forEach { team ->
                        val clientId = "team-sse:${team.id}"
                        val eventData = mapOf(
                            "classId" to lesson.id,
                            "teamId" to team.id
                        )
                        publishSsePort.publishTo(clientId, CLASS_CANCEL_EVENT, eventData)
                        sseEmitterRepository.get(clientId)?.let { emitter ->
                            try {
                                emitter.complete()
                            } catch (_: Exception) {
                                // emitter already completed
                            }
                            sseEmitterRepository.delete(clientId)
                        }
                    }
                    val organClientId = "lesson-organ-sse:${lesson.id}:${lesson.organId}"
                    sseEmitterRepository.get(organClientId)?.let { emitter ->
                        try {
                            emitter.complete()
                        } catch (_: Exception) {
                            // emitter already completed
                        }
                        sseEmitterRepository.delete(organClientId)
                    }
                }
            }
        )
    }
}
