package team.mozu.dsm.application.port.out.lesson.dto

import com.querydsl.core.annotations.QueryProjection

data class LessonItemDetailProjection @QueryProjection constructor(
    val preMoney: Int,
    val curMoney: Int,
    val itemCurrentMoney: Int,
    val itemRound1Money: Int,
    val itemRound2Money: Int,
    val itemRound3Money: Int,
    val itemRound4Money: Int?,
    val itemRound5Money: Int?
)
