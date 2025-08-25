package team.mozu.dsm.adapter.out.team.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.lesson.persistence.repository.LessonRepository
import team.mozu.dsm.adapter.out.team.persistence.mapper.TeamMapper
import team.mozu.dsm.adapter.out.team.persistence.repository.TeamRepository
import team.mozu.dsm.application.port.out.team.TeamPort
import team.mozu.dsm.domain.team.model.Team

@Component
class TeamPersistenceAdapter(
    private val teamRepository: TeamRepository,
    private val lessonRepository: LessonRepository,
    private val teamMapper: TeamMapper
) : TeamPort {
    override fun save(team: Team): Team {
        val lessonEntity = lessonRepository.getReferenceById(team.lessonId)
        val entity = teamMapper.toEntity(team, lessonEntity)

        val saved = teamRepository.save(entity)

        return teamMapper.toModel(saved)
    }
}
