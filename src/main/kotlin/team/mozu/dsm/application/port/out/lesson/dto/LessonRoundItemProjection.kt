package team.mozu.dsm.application.port.out.lesson.dto

import com.querydsl.core.annotations.QueryProjection

data class LessonRoundItemProjection @QueryProjection constructor(
    val itemId: Int,
    val itemName: String,
    val itemLogo: String?,
    val preMoney: Long,
    val curMoney: Long
)
