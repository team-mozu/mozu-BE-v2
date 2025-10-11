package team.mozu.dsm.adapter.`in`.item.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

data class UpdateItemRequest(
    @field:NotBlank(message = "종목 이름은 필수 입력입니다.")
    @field:Size(min = 1, max = 30, message = "종목 이름은 1~30자 이내로 입력해주세요.")
    val itemName: String,

    @field:NotBlank(message = "종목 설명은 필수 입력입니다.")
    @field:Size(min = 1, max = 10000, message = "종목 설명은 1~255자 이내로 입력해주세요.")
    val itemInfo: String,

    val itemLogo: MultipartFile?,

    val itemLogoUrl: String?,

    @field:Min(0, message = "자산은 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "자산은 유효한 범위를 초과할 수 없습니다.")
    val money: Long,

    @field:Min(0, message = "부채는 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "부채는 유효한 범위를 초과할 수 없습니다.")
    val debt: Long,

    @field:Min(0, message = "자본금은 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "자본금은 유효한 범위를 초과할 수 없습니다.")
    val capital: Long,

    @field:Min(0, message = "매출액은 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "매출액은 유효한 범위를 초과할 수 없습니다.")
    val profit: Long,

    @field:Min(0, message = "매출원가는 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "매출원가는 유효한 범위를 초과할 수 없습니다.")
    val profitOg: Long,

    @field:Min(0, message = "매출이익은 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "매출이익은 유효한 범위를 초과할 수 없습니다.")
    val profitBenefit: Long,

    @field:Min(0, message = "당기순이익은 0원 이상이어야 합니다.")
    @field:Max(Long.MAX_VALUE, message = "당기순이익은 유효한 범위를 초과할 수 없습니다.")
    val netProfit: Long
)
