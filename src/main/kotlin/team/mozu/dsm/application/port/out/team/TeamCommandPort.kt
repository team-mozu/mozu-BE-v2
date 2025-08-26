package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Team

interface TeamCommandPort {

    fun save(team: Team): Team
}
