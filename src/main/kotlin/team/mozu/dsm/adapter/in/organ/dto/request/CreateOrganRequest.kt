package team.mozu.dsm.adapter.`in`.organ.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateOrganRequest(
    @field:NotBlank(message = "기관이름을 입력하세요")
    @field:Size(max = 100, message = "기관이름은 100자 이내로 작성해주세요")
    val organName: String,

    @field:NotBlank(message = "기관코드를 입력하세요")
    @field:Size(max = 30, message = "기관코드는 30자 이내로 작성해주세요")
    val organCode: String,

    @field:NotBlank(message = "비밀번호를 입력하세요")
    @field:Size(max = 300, message = "비밀번호는 300자 이내로 작성해주세요")
    val password: String
)
