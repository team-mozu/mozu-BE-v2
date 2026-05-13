package team.mozu.dsm.application.port.out.team

import team.mozu.dsm.domain.team.model.RoundSnapshot
import java.util.UUID

interface RoundSnapshotPort {

    fun save(snapshot: RoundSnapshot): RoundSnapshot

    fun findAllByLessonId(lessonId: UUID): List<RoundSnapshot>

    fun existsByLessonIdAndTeamIdAndInvRound(lessonId: UUID, teamId: UUID, invRound: Int): Boolean
}
