package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.TeamMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.exception.lesson.LessonNotFoundException
import team.mozu.dsm.application.port.out.team.TeamCommandPort
import team.mozu.dsm.domain.team.model.Team

@Component
class TeamCommandPersistenceAdapter(
    private val teamRepository: TeamRepository,
    private val lessonRepository: LessonRepository,
    private val teamMapper: TeamMapper
) : TeamCommandPort {
    override fun save(team: Team): Team {
        val lessonEntity = lessonRepository.findById(team.lessonId)
            .orElseThrow { LessonNotFoundException }
        val entity = teamMapper.toEntity(team, lessonEntity)

        val saved = teamRepository.save(entity)

        return teamMapper.toModel(saved)
    }
}
