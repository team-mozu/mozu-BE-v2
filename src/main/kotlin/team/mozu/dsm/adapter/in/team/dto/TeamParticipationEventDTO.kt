package team.mozu.dsm.adapter.`in`.team.dto

import java.util.UUID

data class TeamParticipationEventDTO(
    val teamId: UUID,
    val teamName: String?,
    val schoolName: String,
    val lessonNum: String
)
