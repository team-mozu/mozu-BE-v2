package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

interface TeamQueryPort {

    fun findById(teamId: UUID): Team?
}
