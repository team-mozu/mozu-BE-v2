package team.mozu.dsm.application.port.`in`.lesson.command

data class UpdateLessonCommand(
    val lessonName: String,
    val baseMoney: Long,
    val lessonRound: Int
)
