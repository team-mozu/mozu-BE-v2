package team.mozu.dsm.application.port.out.lesson.dto

import com.querydsl.core.annotations.QueryProjection
import java.util.*

data class LessonRoundItemProjection @QueryProjection constructor(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val preMoney: Int,
    val curMoney: Int
)
