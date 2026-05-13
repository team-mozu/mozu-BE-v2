package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.ParticipatingTeamResponse
import java.util.UUID

interface QueryParticipatingTeamsUseCase {

    fun execute(lessonId: UUID): List<ParticipatingTeamResponse>
}
