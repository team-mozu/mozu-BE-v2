package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.Team
import java.util.UUID

interface QueryTeamPort {

    fun findById(teamId: UUID): Team?

    fun findByIdWithLock(teamId: UUID): Team?

    fun findAllByLessonNumOrderByTotalMoneyDesc(lessonNum: String): List<Team>

    fun findAllByLessonNum(lessonNum: String): List<Team>

    fun findAllByLessonIdAndInvestmentInProgress(lessonId: UUID): List<Team>

    fun existsByTeamNameAndLessonIdAndLessonNum(teamName: String, lessonId: UUID, lessonNum: String): Boolean
}
