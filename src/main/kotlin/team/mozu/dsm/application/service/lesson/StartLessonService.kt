package team.mozu.dsm.application.service.lesson

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.lesson.dto.response.StartLessonResponse
import team.mozu.dsm.application.port.`in`.lesson.StartLessonUseCase
import team.mozu.dsm.application.port.out.lesson.CommandLessonPort
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import java.security.SecureRandom
import java.util.UUID

@Service
class StartLessonService(
    private val queryLessonPort: QueryLessonPort,
    private val commandLessonPort: CommandLessonPort
): StartLessonUseCase {

    companion object{
        val random: SecureRandom = SecureRandom()
    }

    @Transactional
    override fun start(id: UUID): StartLessonResponse {
        val lesson = queryLessonPort.findById(id)
        var lessonNum: String

        // lessonNum 중복 방지
        do lessonNum = createCode()
        while (queryLessonPort.existsByLessonNum(lessonNum))

        commandLessonPort.updateLessonNum(lesson.id!!, lessonNum)

        return StartLessonResponse(lessonNum)
    }

    private fun createCode(): String {
        val code = random.nextInt(10_000_000) // 0 ~ 9,999,999 숫자 랜덤 발급
        return "%07d".format(code)
    }
}
