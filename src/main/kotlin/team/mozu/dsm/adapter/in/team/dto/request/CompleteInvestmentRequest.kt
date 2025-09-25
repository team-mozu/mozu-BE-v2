package team.mozu.dsm.adapter.`in`.team.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import team.mozu.dsm.domain.team.type.OrderType

data class CompleteInvestmentRequest(
    @field:NotNull(message = "종목 ID는 필수입니다")
    val itemId: Int,

    @field:NotBlank(message = "종목명은 필수입니다")
    val itemName: String,

    @field:Min(value = 1, message = "주문 가격은 1원 이상이어야 합니다")
    val itemPrice: Long,

    @field:Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다")
    val orderCount: Int,

    @field:Min(value = 1, message = "총 금액은 1원 이상이어야 합니다")
    val totalMoney: Long,

    @field:NotNull(message = "주문 타입은 필수입니다")
    val orderType: OrderType
)
