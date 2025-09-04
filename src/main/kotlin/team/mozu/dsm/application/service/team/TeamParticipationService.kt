package team.mozu.dsm.application.service.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.team.dto.TeamParticipationEventDTO
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.exception.lesson.LessonDeletedException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotInProgressException
import team.mozu.dsm.application.exception.lesson.LessonNumNotFoundException
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.`in`.sse.PublishToSseUseCase
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
    private val jwtPort: JwtPort,
    private val publishToSseUseCase: PublishToSseUseCase
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

        /**
         * 트랜잭션 안에서 SSE 이벤트를 직접 발행하면
         * 이벤트 발행 중 예외가 발생할 경우 DB 트랜잭션은 롤백되지만 이미 발행된 이벤트는 취소되지 않음
         * TransactionSynchronizationManager.registerSynchronizatio을 사용해
         * 트랜잭션이 성공적으로 커밋된 이후에만 SSE를 발행함
         */
        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    val eventData = TeamParticipationEventDTO(
                        teamId = savedTeam.id!!, //save한 직후여서 null일 가능성 없음
                        teamName = savedTeam.teamName,
                        schoolName = savedTeam.schoolName,
                        lessonNum = savedTeam.lessonNum
                    )
                    publishToSseUseCase.publishTo(organ.id.toString(), "TEAM_PART_IN", eventData)
                }
            }
        )
        return jwtPort.createStudentAccessToken(
            lesson.lessonNum ?: throw LessonNumNotFoundException,
            savedTeam.id!!
        )
    }
}
