package team.mozu.dsm.adapter.`in`.item.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ItemRequest(
    @field:NotBlank(message = "종목 이름은 필수 입력입니다.")
    @field:Size(min = 1, max = 30, message = "종목 이름은 1~30자 이내로 입력해주세요.")
    val itemName: String,

    @field:NotBlank(message = "종목 설명은 필수 입력입니다.")
    @field:Size(min = 1, max = 255, message = "종목 설명은 1~255자 이내로 입력해주세요.")
    val itemInfo: String,

    @field:Min(1, message = "자산은 최소 1원 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "자산은 100억 이하로 입력해야 합니다.")
    val money: Int,

    @field:Min(0, message = "부채는 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "부채는 100억 이하로 입력해야 합니다.")
    val debt: Int,

    @field:Min(0, message = "자본금은 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "자본금은 100억 이하로 입력해야 합니다.")
    val capital: Int,

    @field:Min(0, message = "매출액은 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "매출액은 100억 이하로 입력해야 합니다.")
    val profit: Int,

    @field:Min(0, message = "매출원가는 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "매출원가는 100억 이하로 입력해야 합니다.")
    val profitOg: Int,

    @field:Min(0, message = "매출이익은 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "매출이익은 100억 이하로 입력해야 합니다.")
    val profitBenefit: Int,

    @field:Min(0, message = "당기순이익은 0 이상이어야 합니다.")
    @field:Max(10_000_000_000, message = "당기순이익은 100억 이하로 입력해야 합니다.")
    val netProfit: Int
)
