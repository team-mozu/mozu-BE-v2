package team.mozu.dsm.adapter.`in`.lesson.dto.response

import team.mozu.dsm.application.port.out.lesson.dto.LessonItemDetailProjection
import team.mozu.dsm.domain.item.model.Item
import kotlin.math.roundToInt

data class LessonItemDetailResponse(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val nowMoney: Long,
    val profitMoney: Long,
    val profitNum: String,
    val moneyList: List<Long>,
    val itemInfo: String,
    val money: Long,
    val debt: Long,
    val capital: Long,
    val profit: Long,
    val profitOg: Long,
    val profitBen: Long,
    val netProfit: Long
) {
    companion object {
        fun of(item: Item, projection: LessonItemDetailProjection, curInvRound: Int): LessonItemDetailResponse {
            val moneyList = listOf(
                projection.round1Price,
                projection.round2Price,
                projection.round3Price,
                projection.round4Price ?: 0,
                projection.round5Price ?: 0
            ).take(curInvRound)

            val profitMoney = if (projection.preMoney != 0L) {
                projection.curMoney - projection.preMoney
            } else {
                0L
            }

            val profitNum = if (projection.preMoney != 0L) {
                ((profitMoney.toDouble() * 100 / projection.preMoney) * 100).roundToInt() / 100.0
            } else {
                0.0
            }

            val formattedProfitNum = if (profitNum > 0) "+$profitNum%" else "$profitNum%"

            return LessonItemDetailResponse(
                itemId = item.id!!,
                itemName = item.itemName,
                itemLogo = item.itemLogo,
                nowMoney = projection.curMoney,
                profitMoney = profitMoney,
                profitNum = formattedProfitNum,
                moneyList = moneyList,
                itemInfo = item.itemInfo,
                money = item.money,
                debt = item.debt,
                capital = item.capital,
                profit = item.profit,
                profitOg = item.profitOg,
                profitBen = item.profitBenefit,
                netProfit = item.netProfit
            )
        }
    }
}
