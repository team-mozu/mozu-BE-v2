package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

interface QueryTeamPort {

    fun findById(teamId: UUID): Team

    fun findByIdWithLock(teamId: UUID): Team?

    fun findAllByLessonId(lessonId: UUID): List<Team>
}
