package team.mozu.dsm.adapter.`in`.team

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.team.port.`in`.TeamParticipationUseCase

@RestController
@RequestMapping("/team")
class TeamWebAdapter(
    private val teamParticipationUseCase: TeamParticipationUseCase
) {
    @PostMapping("/participate")
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(@RequestBody request: TeamParticipationRequest): TeamTokenResponse {
        return teamParticipationUseCase.execute(request)
    }
}
