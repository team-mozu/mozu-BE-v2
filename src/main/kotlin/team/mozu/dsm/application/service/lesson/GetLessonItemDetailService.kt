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

        // 전체 money 배열: [currentMoney, round1Money, round2Money, round3Money, round4Money, round5Money]
        val fullMoneyArray = listOf(
            lessonItem.itemCurrentMoney,     // 0번째 (건너뜀)
            lessonItem.itemRound1Money,      // 1번째 -> 1차
            lessonItem.itemRound2Money,      // 2번째 -> 2차  
            lessonItem.itemRound3Money,      // 3번째 -> 3차
            lessonItem.itemRound4Money ?: 0, // 4번째 -> 4차
            lessonItem.itemRound5Money ?: 0  // 5번째 -> 5차
        )
        
        // 0번째 인덱스를 건너뛰고 1번째부터 사용
        val moneyList = fullMoneyArray.drop(1).take(lesson.curInvRound)

        val profitMoney = if (lessonItem.preMoney != 0L) {
            lessonItem.curMoney - lessonItem.preMoney
        } else {
            0
        }
        val profitNum = if (lessonItem.preMoney != 0L) {
            ((profitMoney.toDouble() * 100 / lessonItem.preMoney) * 100).roundToInt() / 100.0
        } else {
            0.0
        }

        val formattedProfitNum = if (profitNum > 0) "+$profitNum%" else "$profitNum%"

        return LessonItemDetailResponse(
            itemId = item.id,
            itemName = item.itemName,
            itemLogo = item.itemLogo,
            nowMoney = lessonItem.curMoney,
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
