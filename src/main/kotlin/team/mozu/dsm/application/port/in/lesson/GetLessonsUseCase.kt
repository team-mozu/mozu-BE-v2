package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse

interface GetLessonsUseCase {

    fun get(): LessonListResponse
}
