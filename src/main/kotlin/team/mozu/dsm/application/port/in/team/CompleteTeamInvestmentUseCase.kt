package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.adapter.`in`.team.dto.request.CompleteInvestmentRequest
import java.util.UUID

interface CompleteTeamInvestmentUseCase {

    fun completeInvestment(requests: List<CompleteInvestmentRequest>, lessonNum: String, teamId: UUID)
}
