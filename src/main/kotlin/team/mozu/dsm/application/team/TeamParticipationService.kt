package team.mozu.dsm.application.team

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import team.mozu.dsm.adapter.`in`.team.dto.TeamParticipationEventDTO
import team.mozu.dsm.application.exception.lesson.*
import team.mozu.dsm.application.port.`in`.sse.PublishToAllSseUseCase
import team.mozu.dsm.application.port.out.auth.JwtPort
import team.mozu.dsm.application.port.out.lesson.LessonQueryPort
import team.mozu.dsm.application.port.out.team.TeamCommandPort
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.application.port.`in`.team.dto.request.TeamParticipationCommand
import team.mozu.dsm.application.port.`in`.team.dto.response.TeamToken
import team.mozu.dsm.domain.team.model.Team
import java.time.LocalDateTime

@Service
class TeamParticipationService(
    private val lessonQueryPort: LessonQueryPort,
    private val teamCommandPort: TeamCommandPort,
    private val jwtPort: JwtPort,
    private val publishToAllSseUseCase: PublishToAllSseUseCase
) : TeamParticipationUseCase {

    @Transactional
    override fun participate(command: TeamParticipationCommand): TeamToken {
        val lesson = lessonQueryPort.findByLessonNum(command.lessonNum)
            ?: throw LessonNumNotFoundException

        if (!lesson.isInProgress) {
            throw LessonNotInProgressException
        }

        if (lesson.isDeleted) {
            throw LessonDeletedException
        }

        val team = Team(
            id = null,
            lessonId = lesson.id ?: throw LessonIdNotFoundException,
            teamName = command.teamName,
            schoolName = command.schoolName,
            totalMoney = 0L,
            cashMoney = 0L,
            valuationMoney = 0L,
            lessonNum = command.lessonNum,
            isInvestmentInProgress = true, //투자 종료 시 false
            participationDate = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val savedTeam = teamCommandPort.save(team)

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
                        teamName = savedTeam.teamName!!,
                        schoolName = savedTeam.schoolName,
                        lessonNum = savedTeam.lessonNum
                    )
                    publishToAllSseUseCase.publishToAll("EVENT_TEAM_PARTICIPATION", eventData)
                }
            }
        )
        return jwtPort.createStudentAccessToken(lesson.lessonNum)
    }
}
