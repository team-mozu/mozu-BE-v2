package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonRoundItemResponse

interface GetLessonRoundItemsUseCase {

    fun execute(lessonNum: String): List<LessonRoundItemResponse>
}
