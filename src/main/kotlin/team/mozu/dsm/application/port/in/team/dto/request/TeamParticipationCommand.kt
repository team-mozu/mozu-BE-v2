package team.mozu.dsm.application.port.`in`.team.dto.request

import jakarta.validation.constraints.NotBlank

data class TeamParticipationCommand(
    @field:NotBlank(message = "lessonNum은 필수입니다")
    val lessonNum: String,
    @field:NotBlank(message = "schoolName은 필수입니다")
    val schoolName: String,
    @field:NotBlank(message = "teamName은 필수입니다")
    val teamName: String
)
