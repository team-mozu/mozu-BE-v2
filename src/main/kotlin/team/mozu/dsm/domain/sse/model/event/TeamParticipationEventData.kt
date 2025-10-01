package team.mozu.dsm.domain.sse.model.event

import java.util.UUID

/**
 * 팀 참여 이벤트 데이터
 */
data class TeamParticipationEventData(
    val teamId: UUID,
    val teamName: String?,
    val schoolName: String,
    val lessonNum: String,
    val participantCount: Int
)
