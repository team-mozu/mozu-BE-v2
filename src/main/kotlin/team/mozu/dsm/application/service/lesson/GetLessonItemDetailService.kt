package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemDetailResponse
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.GetLessonItemDetailUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import kotlin.math.roundToInt

@Service
class GetLessonItemDetailService(
    private val lessonPort: QueryLessonPort,
    private val itemPort: QueryItemPort,
    private val lessonItemPort: QueryLessonItemPort
) : GetLessonItemDetailUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonNum: String, itemId: Int): LessonItemDetailResponse {
        val lesson = lessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException
        val item = itemPort.findById(itemId)
            ?: throw ItemNotFoundException
        val lessonItem = lessonItemPort.findItemDetailByLessonIdAndItemId(lesson.id!!, item.id!!)

        val itemMoneyList = listOf(
            lessonItem.itemCurrentMoney,
            lessonItem.itemRound1Money,
            lessonItem.itemRound2Money,
            lessonItem.itemRound3Money,
            lessonItem.itemRound4Money ?: 0,
            lessonItem.itemRound5Money ?: 0
        )
        val moneyList = itemMoneyList.take(lesson.curInvRound)

        val profitMoney = if (lessonItem.preMoney != 0) {
            lessonItem.curMoney - lessonItem.preMoney
        } else {
            0
        }
        val profitNum = if (lessonItem.preMoney != 0) {
            ((profitMoney.toDouble() * 100 / lessonItem.preMoney) * 100).roundToInt() / 100.0
        } else {
            0.0
        }

        return LessonItemDetailResponse(
            itemId = item.id,
            itemName = item.itemName,
            itemLogo = item.itemLogo,
            nowMoney = lessonItem.curMoney,
            profitMoney = profitMoney,
            profitNum = profitNum,
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
