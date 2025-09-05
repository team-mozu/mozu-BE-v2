package team.mozu.dsm.adapter.`in`.lesson.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

// 수업 생성, 수정 API에서 공통 사용
data class LessonRequest(
    @field:NotBlank(message = "수업 이름은 필수 입력입니다.")
    @field:Size(min = 1, max = 30, message = "수업 이름은 1~30자 이내로 입력해주세요.")
    val lessonName: String,

    @field:Min(100_000, message = "기초 자산은 최소 100,000원 이상 입력해야 합니다.")
    @field:Max(2_147_483_647, message = "기초 자산은 2,147,483,647원 이하로 입력해야 합니다.")
    val baseMoney: Int,

    @field:Min(3, message = "투자 차수는 최소 3차부터 가능합니다.")
    @field:Max(5, message = "투자 차수는 최대 5차까지 가능합니다.")
    val lessonRound: Int,

    @field:Size(min = 1, max = 20, message = "수업 종목은 1~20개 사이로 입력해야 합니다.")
    val lessonItems: List<LessonItemRequest>,

    @field:Valid
    @field:Size(min = 1, message = "수업 기사는 최소 1개 이상 입력해야 합니다.")
    val lessonArticles: List<LessonArticleRequest>
)

data class LessonItemRequest(
    val id: UUID,
    val money: List<Int>
)

data class LessonArticleRequest(
    val investmentRound: Int,

    @field:Size(max = 20, message = "차수마다 기사는 최대 20개까지 입력할 수 있습니다.")
    val articles: List<UUID>
)
