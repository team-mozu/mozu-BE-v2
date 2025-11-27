package team.mozu.dsm.adapter.`in`.team.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class TeamParticipationRequest(

    @field:Size(min = 4, max = 4, message = "수업 번호는 4자로 입력해주세요.")
    @field:NotBlank(message = "수업 번호는 필수입니다.")
    val lessonNum: String,

    @field:Size(max = 100, message = "학교 이름은 100자 이내로 입력해주세요.")
    @field:NotBlank(message = "학교 이름은 필수입니다.")
    val schoolName: String,

    @field:Size(max = 100, message = "팀 이름은 100자 이내로 입력해주세요.")
    @field:NotBlank(message = "팀 이름은 필수입니다.")
    val teamName: String
)
