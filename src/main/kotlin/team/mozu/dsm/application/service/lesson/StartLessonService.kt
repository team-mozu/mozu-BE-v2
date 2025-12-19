package team.mozu.dsm.application.service.lesson

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.lesson.dto.NextLessonEventDTO
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.application.exception.lesson.CannotStartLessonException
import team.mozu.dsm.application.port.`in`.lesson.StartLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.sse.PublishSsePort
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.security.SecureRandom
import java.util.UUID

@Service
class StartLessonService(
    private val lessonFacade: LessonFacade,
    private val commandLessonPort: CommandLessonPort,
    private val securityPort: SecurityPort,
    private val publishSsePort: PublishSsePort,
    private val teamPort: QueryTeamPort
) : StartLessonUseCase {

    companion object {
        val random: SecureRandom = SecureRandom()
        private const val START_LESSON_EVENT = "CLASS_START"
    }

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun start(id: UUID): StartLessonResponse {
        val lesson = lessonFacade.findByLessonId(id)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotStartLessonException
        }

        var lessonNum: String

        // lessonNum 중복 방지 + 재시도
        while (true) {
            lessonNum = createCode()
            try {
                commandLessonPort.updateLessonNumAndIsInProgress(lesson.id!!, lessonNum)
                val updatedLesson = lessonFacade.findByLessonId(lesson.id)

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
                                    publishSsePort.publishTo("team-sse:${team.id}", START_LESSON_EVENT, eventData)
                                } catch (e: Exception) {
                                    // 개별 팀 이벤트 전송 실패가 다른 팀에 영향을 주지 않도록 예외 처리
                                    log.error("Failed to send CLASS_START event to team ${team.id}", e)
                                }
                            }
                        }
                    }
                )

                break // 성공하면 루프 종료
            } catch (e: DataIntegrityViolationException) {
                // 유니크 충돌 시 재시도
            }
        }

        return StartLessonResponse(lessonNum)
    }

    private fun createCode(): String {
        val code = random.nextInt(10_000) // 0 ~ 9,999 숫자 랜덤 발급
        return "%04d".format(code)
    }
}
