package team.mozu.dsm.global.security.auth

import java.util.UUID

data class StudentPrincipal(
    val lessonNum: String,
    val teamId: UUID
) : java.security.Principal {
    override fun getName(): String = lessonNum
}
