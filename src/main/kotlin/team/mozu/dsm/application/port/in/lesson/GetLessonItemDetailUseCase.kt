package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonItemDetailResponse

interface GetLessonItemDetailUseCase {

    fun get(lessonNum: String, itemId: Int): LessonItemDetailResponse
}
