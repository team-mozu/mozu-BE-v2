package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.LessonListResponse
import team.mozu.dsm.application.port.`in`.lesson.QueryLessonsUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort

@Service
class QueryLessonsService(
    private val securityPort: SecurityPort,
    private val lessonPort: QueryLessonPort
) : QueryLessonsUseCase {

    @Transactional(readOnly = true)
    override fun execute(): LessonListResponse {
        val organ = securityPort.getCurrentOrgan()
        val lessonList = lessonPort.findAllByOrganId(organ.id!!)

        return LessonListResponse(lessonList)
    }
}
