package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.ParticipatingTeamResponse
import team.mozu.dsm.application.port.`in`.lesson.QueryParticipatingTeamsUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.exception.lesson.CannotLessonSSEConnectException
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID

@Service
class QueryParticipatingTeamsService(
    private val lessonFacade: LessonFacade,
    private val queryTeamPort: QueryTeamPort,
    private val securityPort: SecurityPort
) : QueryParticipatingTeamsUseCase {

    override fun execute(lessonId: UUID): List<ParticipatingTeamResponse> {
        val lesson = lessonFacade.findByLessonId(lessonId)
        val organ = securityPort.getCurrentOrgan()

        if (lesson.organId != organ.id) {
            throw CannotLessonSSEConnectException
        }

        return queryTeamPort.findAllByLessonId(lessonId)
            .map(ParticipatingTeamResponse::of)
    }
}
