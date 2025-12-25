package team.mozu.dsm.application.port.`in`.lesson

import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonArticleResponse
import java.util.UUID

interface GetLessonArticlesUseCase {

    fun execute(lessonId: UUID): List<LessonArticleResponse>
}
