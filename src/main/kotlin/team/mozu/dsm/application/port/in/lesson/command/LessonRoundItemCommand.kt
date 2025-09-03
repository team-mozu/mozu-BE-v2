package team.mozu.dsm.application.port.`in`.lesson.command

import com.querydsl.core.annotations.QueryProjection
import java.util.UUID

data class LessonRoundItemCommand @QueryProjection constructor(
    val itemId: UUID,
    val itemName: String,
    val itemLogo: String?,
    val preMoney: Int,
    val curMoney: Int
)
