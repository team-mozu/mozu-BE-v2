package team.mozu.dsm.application.port.out.lesson.dto

import com.querydsl.core.annotations.QueryProjection

data class LessonItemDetailProjection @QueryProjection constructor(
    val preMoney: Long,
    val curMoney: Long,
    val itemCurrentMoney: Long,
    val itemRound1Money: Long,
    val itemRound2Money: Long,
    val itemRound3Money: Long,
    val itemRound4Money: Long?,
    val itemRound5Money: Long?
)
