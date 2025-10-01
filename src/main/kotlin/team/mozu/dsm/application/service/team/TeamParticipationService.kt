package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.exception.lesson.LessonDeletedException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotInProgressException
import team.mozu.dsm.application.exception.lesson.LessonNumNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.team.CommandTeamPort
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.application.port.out.organ.QueryOrganPort
import team.mozu.dsm.domain.team.model.Team
import java.time.LocalDateTime

@Service
class TeamParticipationService(
    private val queryLessonPort: QueryLessonPort,
    private val commandTeamPort: CommandTeamPort,
    private val queryOrganPort: QueryOrganPort,
    private val jwtPort: JwtPort
) : TeamParticipationUseCase {

    @Transactional
    override fun participate(request: TeamParticipationRequest): TeamTokenResponse {
        val lesson = queryLessonPort.findByLessonNum(request.lessonNum)
            ?: throw LessonNumNotFoundException

        val organ = queryOrganPort.findModelById(lesson.organId)
            ?: throw OrganNotFoundException

        if (!lesson.isInProgress) {
            throw LessonNotInProgressException
        }

        if (lesson.isDeleted) {
            throw LessonDeletedException
        }

        val baseMoney = lesson.baseMoney

        val team = Team(
            id = null,
            lessonId = lesson.id ?: throw LessonNotFoundException,
            teamName = request.teamName,
            schoolName = request.schoolName,
            totalMoney = baseMoney,
            cashMoney = baseMoney,
            valuationMoney = 0,
            lessonNum = request.lessonNum,
            isInvestmentInProgress = true, //투자 종료 시 false
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val savedTeam = commandTeamPort.create(team)
        return jwtPort.createStudentAccessToken(
            lesson.lessonNum ?: throw LessonNumNotFoundException,
            savedTeam.id!!
        )
    }
}
