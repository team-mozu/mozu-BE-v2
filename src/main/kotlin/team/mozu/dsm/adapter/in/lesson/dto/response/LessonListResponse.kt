package team.mozu.dsm.adapter.`in`.lesson.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime
import java.util.*

data class LessonListResponse(
    val lessons: List<LessonSummaryResponse>
)

data class LessonSummaryResponse @QueryProjection constructor(
    val id: UUID,
    val name: String,
    val isStarred: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDateTime
)
