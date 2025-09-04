package team.mozu.dsm.adapter.`in`.article.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ArticleRequest(
    @field:NotBlank(message = "기사 제목은 필수 입력입니다.")
    @field:Size(min = 1, max = 300, message = "기사 제목은 1~300자 이내로 입력해주세요.")
    val articleName: String,

    @field:NotBlank(message = "기사 본문 내용은 필수 입력입니다.")
    @field:Size(min = 1, max = 10_000, message = "기사 본문은 1~10000자 이내로 입력해주세요.")
    val articleDesc: String,

    @field:Size(max = 2000, message = "이미지 URL은 최대 2000자까지 입력 가능합니다.")
    val articleImage: String? = null
)
