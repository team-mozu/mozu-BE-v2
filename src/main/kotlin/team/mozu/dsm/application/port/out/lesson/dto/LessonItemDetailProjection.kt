package team.mozu.dsm.application.port.out.lesson.dto

import com.querydsl.core.annotations.QueryProjection

data class LessonItemDetailProjection @QueryProjection constructor(
    val preMoney: Long,
    val curMoney: Long,
    val round1Price: Long,
    val round2Price: Long,
    val round3Price: Long,
    val round4Price: Long?,
    val round5Price: Long?,
    val endPrice: Long
)
