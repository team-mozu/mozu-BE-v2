package team.mozu.dsm.adapter.`in`.team

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.application.port.`in`.team.dto.request.TeamParticipationCommand

@RestController
@RequestMapping("/team")
class TeamWebAdapter(
    private val teamParticipationUseCase: TeamParticipationUseCase
) {
    @PostMapping("/participate")
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(@Valid @RequestBody request: TeamParticipationRequest): TeamTokenResponse {
        val command = TeamParticipationCommand(
            lessonNum = request.lessonNum,
            schoolName = request.schoolName,
            teamName = request.teamName
        )

        val token = teamParticipationUseCase.participate(command)

        return TeamTokenResponse(
            accessToken = token.accessToken,
            accessExpiredAt = token.accessExpiredAt
        )
    }
}
