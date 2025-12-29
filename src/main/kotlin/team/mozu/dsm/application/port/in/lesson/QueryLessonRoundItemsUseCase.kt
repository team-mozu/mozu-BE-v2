package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse

interface QueryLessonRoundItemsUseCase {

    fun execute(lessonNum: String): List<LessonRoundItemResponse>
}
