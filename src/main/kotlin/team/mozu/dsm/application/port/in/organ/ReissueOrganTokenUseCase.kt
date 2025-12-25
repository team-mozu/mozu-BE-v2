package team.mozu.dsm.application.port.`in`.organ

import team.mozu.dsm.adapter.`in`.auth.dto.response.TokenResponse
import team.mozu.dsm.adapter.`in`.organ.dto.request.ReissueOrganTokenRequest

interface ReissueOrganTokenUseCase {

    fun execute(request: ReissueOrganTokenRequest): TokenResponse
}
