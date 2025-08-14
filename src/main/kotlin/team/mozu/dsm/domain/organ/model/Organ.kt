package team.mozu.dsm.domain.organ.model

import java.util.UUID

data class Organ(
    val id: UUID?,
    val organCode: String,
    val organName: String,
    val password: String
)
