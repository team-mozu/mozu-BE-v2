package team.mozu.dsm.application.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.exception.lesson.LessonIdNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotInProgressException
import team.mozu.dsm.application.exception.lesson.LessonNumNotFoundException
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.application.port.out.lesson.LessonPort
import team.mozu.dsm.application.port.out.team.TeamPort
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.domain.team.model.Team
import java.time.LocalDateTime
import java.util.UUID

@Service
class TeamParticipationService(
    private val lessonPort: LessonPort,
    private val teamPort: TeamPort,
    private val jwtPort: JwtPort
) : TeamParticipationUseCase {

    @Transactional
    override fun execute(request: TeamParticipationRequest): TeamTokenResponse {
        val lesson = lessonPort.findByLessonNum(request.lessonNum)
            ?: throw LessonNumNotFoundException

        if(!lesson.isInProgress) {
            throw LessonNotInProgressException
        }

        val team = Team(
            id = UUID.randomUUID(),
            lessonId = lesson.id ?: throw LessonIdNotFoundException,
            teamName = request.teamName,
            schoolName = request.schoolName,
            totalMoney = 0L,
            cashMoney = 0L,
            valuationMoney = 0L,
            lessonNum = lesson.lessonNum,
            isInvestmentInProgress = true, //투자 종료 시 false
            participationDate = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        teamPort.save(team)

        return jwtPort.createStudentAccessToken(lesson.lessonNum)
    }
}
