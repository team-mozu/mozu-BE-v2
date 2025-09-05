package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Team

interface CommandTeamPort {

    fun create(team: Team): Team

    fun update(team: Team): Team
}
