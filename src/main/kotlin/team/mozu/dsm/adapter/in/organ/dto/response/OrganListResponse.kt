package team.mozu.dsm.adapter.`in`.organ.dto.response

import com.querydsl.core.annotations.QueryProjection
import java.util.UUID

data class OrganListResponse @QueryProjection constructor(
    val id: UUID,
    val organCode: String,
    val organName: String,
    val password: String
)

