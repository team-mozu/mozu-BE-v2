package team.mozu.dsm.adapter.out.team.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.mozu.dsm.adapter.out.team.entity.RoundSnapshotJpaEntity
import java.util.UUID

interface RoundSnapshotJpaRepository : JpaRepository<RoundSnapshotJpaEntity, UUID> {

    fun findAllByLessonIdOrderByTeamIdAscInvRoundAsc(lessonId: UUID): List<RoundSnapshotJpaEntity>

    fun existsByLessonIdAndTeamIdAndInvRound(lessonId: UUID, teamId: UUID, invRound: Int): Boolean
}
