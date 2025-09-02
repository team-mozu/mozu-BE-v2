package team.mozu.dsm.application.port.`in`.lesson.command

data class UpdateLessonCommand(
    val lessonName: String,
    val baseMoney: Int,
    val lessonRound: Int
)
