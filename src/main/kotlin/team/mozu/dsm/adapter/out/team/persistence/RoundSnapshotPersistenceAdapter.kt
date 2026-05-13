package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonJpaRepository
import team.mozu.dsm.adapter.out.team.mapper.RoundSnapshotMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.RoundSnapshotJpaRepository
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamJpaRepository
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.exception.team.TeamNotFoundException
import team.mozu.dsm.application.port.out.team.RoundSnapshotPort
import team.mozu.dsm.domain.team.model.RoundSnapshot
import java.util.UUID

@Component
class RoundSnapshotPersistenceAdapter(
    private val roundSnapshotJpaRepository: RoundSnapshotJpaRepository,
    private val lessonJpaRepository: LessonJpaRepository,
    private val teamJpaRepository: TeamJpaRepository,
    private val mapper: RoundSnapshotMapper
) : RoundSnapshotPort {

    override fun save(snapshot: RoundSnapshot): RoundSnapshot {
        val lesson = lessonJpaRepository.findByIdOrNull(snapshot.lessonId)
            ?: throw LessonNotFoundException
        val team = teamJpaRepository.findByIdOrNull(snapshot.teamId)
            ?: throw TeamNotFoundException
        val entity = mapper.toEntity(snapshot, lesson, team)
        val saved = roundSnapshotJpaRepository.save(entity)
        return mapper.toModel(saved)
    }

    override fun findAllByLessonId(lessonId: UUID): List<RoundSnapshot> =
        roundSnapshotJpaRepository.findAllByLessonIdOrderByTeamIdAscInvRoundAsc(lessonId)
            .map(mapper::toModel)

    override fun existsByLessonIdAndTeamIdAndInvRound(lessonId: UUID, teamId: UUID, invRound: Int): Boolean =
        roundSnapshotJpaRepository.existsByLessonIdAndTeamIdAndInvRound(lessonId, teamId, invRound)
}
