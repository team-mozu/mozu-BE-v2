package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse
import team.mozu.dsm.application.port.`in`.lesson.GetLessonRoundItemsUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.service.lesson.facade.LessonFacade
import java.util.UUID
import kotlin.math.roundToInt

@Service
class GetLessonRoundItemsService(
    private val lessonFacade: LessonFacade,
    private val lessonItemPort: QueryLessonItemPort
) : GetLessonRoundItemsUseCase {

    @Transactional(readOnly = true)
    override fun get(lessonId: UUID): List<LessonRoundItemResponse> {
        val lesson = lessonFacade.findByLessonId(lessonId)
        val lessonItems = lessonItemPort.findAllRoundItemsByLessonId(lesson.id!!)

        return lessonItems.map { item ->
            val nowMoney = item.curMoney
            val profitMoney = if (item.preMoney != 0) {
                item.curMoney - item.preMoney
            } else {
                0
            }
            val profitNum = if (item.preMoney != 0) {
                ((profitMoney.toDouble() * 100 / item.preMoney) * 100).roundToInt() / 100.0
            } else {
                0.0
            }

            LessonRoundItemResponse(
                itemId = item.itemId,
                itemName = item.itemName,
                itemLogo = item.itemLogo,
                nowMoney = nowMoney,
                profitMoney = profitMoney,
                profitNum = profitNum
            )
        }
    }
}
