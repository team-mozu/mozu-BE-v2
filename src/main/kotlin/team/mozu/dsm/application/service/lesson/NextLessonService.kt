package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.lesson.CannotNextLessonException
import team.mozu.dsm.application.port.`in`.lesson.NextLessonUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class NextLessonService(
    private val lessonFacade: LessonFacade,
    private val lessonPort: CommandLessonPort,
    private val securityPort: SecurityPort,
    private val adminSseService: team.mozu.dsm.application.service.admin.AdminSseServiceImpl
) : NextLessonUseCase {

    @Transactional
    override fun next(lessonId: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val lesson = lessonFacade.findByLessonId(lessonId)

        if (lesson.organId != organ.id) {
            throw CannotNextLessonException
        }

        // 다음 차수 진행을 위해 curInvRound 업데이트
        lessonPort.updateCurInvRound(lesson.id!!)

        // 업데이트된 lesson 정보 다시 조회
        val updatedLesson = lessonFacade.findByLessonId(lessonId)

        // 모든 팀에게 다음 투자 라운드 시작 알림
        adminSseService.startNextInvestmentRound(lesson.id, updatedLesson.curInvRound)
    }
}
