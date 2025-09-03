package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse
import java.util.UUID

interface GetLessonRoundItemsUseCase {

    fun get(lessonNum: String): List<LessonRoundItemResponse>
}
