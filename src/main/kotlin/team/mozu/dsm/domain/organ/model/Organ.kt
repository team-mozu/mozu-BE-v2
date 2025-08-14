package team.mozu.dsm.domain.organ.model

import java.util.UUID

data class Organ(
    val id: UUID?,
    val adminCode: String,
    val orgName: String,
    val password: String
)

