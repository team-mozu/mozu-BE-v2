package team.mozu.dsm.adapter.`in`.organ.dto.response

import java.util.UUID

data class OrganListResponse(
    val id: UUID,
    val organCode: String,
    val organName: String,
    val password: String
)

