package team.mozu.dsm.adapter.`in`.organ.dto.request

import jakarta.validation.constraints.NotBlank

data class ReissueOrganTokenRequest(
    @field:NotBlank(message = "refreshToken을 입력하세요")
    val token: String
)
