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

@RestController
@RequestMapping("/team")
class TeamWebAdapter(
    private val teamParticipationUseCase: TeamParticipationUseCase
) {
    @PostMapping("/participate")
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(@Valid @RequestBody request: TeamParticipationRequest): TeamTokenResponse {
        return teamParticipationUseCase.participate(request)
    }
}
