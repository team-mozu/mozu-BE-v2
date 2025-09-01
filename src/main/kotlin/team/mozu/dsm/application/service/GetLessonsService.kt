package team.mozu.dsm.application.service

import org.springframework.stereotype.Service
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse
import team.mozu.dsm.application.port.`in`.lesson.GetLessonsUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort

@Service
class GetLessonsService(
    private val securityPort: SecurityPort,
    private val lessonPort: QueryLessonPort
): GetLessonsUseCase {

    override fun get(): LessonListResponse {
        val organ = securityPort.getCurrentOrgan()
        val lessonList = lessonPort.findAllByOrganId(organ.id!!)

        return LessonListResponse(lessonList)
    }
}
