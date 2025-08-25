package team.mozu.dsm.adapter.`in`.team.dto.request

import jakarta.validation.constraints.NotBlank

data class TeamParticipationRequest(

    @field:NotBlank(message = "lessonNum은 비어있을 수 없습니다.")
    val lessonNum: String,
    @field:NotBlank(message = "schoolName은 비어있을 수 없습니다.")
    val schoolName: String,
    @field:NotBlank(message = "teamName은 비어있을 수 없습니다.")
    val teamName: String
)
