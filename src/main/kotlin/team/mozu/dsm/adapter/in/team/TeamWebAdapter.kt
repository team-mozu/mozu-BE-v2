package team.mozu.dsm.adapter.`in`.team

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import team.mozu.dsm.adapter.`in`.team.dto.request.TeamParticipationRequest
import team.mozu.dsm.adapter.`in`.team.dto.response.StockResponse
import team.mozu.dsm.adapter.`in`.team.dto.response.TeamTokenResponse
import team.mozu.dsm.application.port.`in`.team.CompleteTeamInvestmentUseCase
import team.mozu.dsm.application.port.`in`.team.GetStocksUseCase
import team.mozu.dsm.application.port.`in`.team.TeamParticipationUseCase
import team.mozu.dsm.global.security.auth.StudentPrincipal

@RestController
@RequestMapping("/team")
class TeamWebAdapter(
    private val teamParticipationUseCase: TeamParticipationUseCase,
    private val teamInvestmentUseCase: CompleteTeamInvestmentUseCase,
    private val getStocksUseCase: GetStocksUseCase
) {
    @PostMapping("/participate")
    @ResponseStatus(HttpStatus.CREATED)
    fun participate(
        @Valid @RequestBody
        request: TeamParticipationRequest
    ): TeamTokenResponse {
        return teamParticipationUseCase.participate(request)
    }

    @PostMapping("/end")
    @ResponseStatus(HttpStatus.CREATED)
    fun endInvestment(
        @Valid @RequestBody
        request: List<@Valid CompleteInvestmentRequest>,
        @AuthenticationPrincipal principal: StudentPrincipal
    ) {
        teamInvestmentUseCase.completeInvestment(request, principal.lessonNum, principal.teamId)
    }

    @GetMapping("/stocks")
    @ResponseStatus(HttpStatus.OK)
    fun stocks(
        @AuthenticationPrincipal principal: StudentPrincipal
    ): List<StockResponse> {
        return getStocksUseCase.getStocks(principal.lessonNum, principal.teamId)
    }
}
