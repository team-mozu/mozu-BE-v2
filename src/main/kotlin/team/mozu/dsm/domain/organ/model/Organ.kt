package team.mozu.dsm.domain.organ.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.util.UUID

@Aggregate
data class Organ(
    val id: UUID?,
    val organCode: String,
    val organName: String,
    val password: String
)
