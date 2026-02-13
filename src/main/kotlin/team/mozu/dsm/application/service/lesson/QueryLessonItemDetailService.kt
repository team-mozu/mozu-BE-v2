package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemDetailResponse
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonItemDetailUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort

@Service
class QueryLessonItemDetailService(
    private val lessonPort: QueryLessonPort,
    private val itemPort: QueryItemPort,
    private val lessonItemPort: QueryLessonItemPort
) : QueryLessonItemDetailUseCase {

    @Transactional(readOnly = true)
    override fun execute(lessonNum: String, itemId: Int): LessonItemDetailResponse {
        val lesson = lessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException
        val item = itemPort.findById(itemId)
            ?: throw ItemNotFoundException
        val lessonItem = lessonItemPort.findItemDetailByLessonIdAndItemId(lesson.id!!, item.id!!)

        return LessonItemDetailResponse.of(item, lessonItem, lesson.curInvRound)
    }
}
