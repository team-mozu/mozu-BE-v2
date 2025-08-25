package team.mozu.dsm.application.port.`in`.team.dto.request

data class TeamParticipationCommand (
    val lessonNum: String,
    val schoolName: String,
    val teamName: String
)
