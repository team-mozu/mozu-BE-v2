package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonRoundItemsUseCase
import team.mozu.dsm.application.port.out.lesson.QueryLessonItemPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort

@Service
class QueryLessonRoundItemsService(
    private val lessonPort: QueryLessonPort,
    private val lessonItemPort: QueryLessonItemPort
) : QueryLessonRoundItemsUseCase {

    @Transactional(readOnly = true)
    override fun execute(lessonNum: String): List<LessonRoundItemResponse> {
        val lesson = lessonPort.findByLessonNum(lessonNum)
            ?: throw LessonNotFoundException

        return lessonItemPort.findAllRoundItemsByLessonId(lesson.id!!)
            .map { LessonRoundItemResponse.from(it) }
    }
}
