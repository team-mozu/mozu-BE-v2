package team.mozu.dsm.domain.item.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Item (
    val id : UUID?,
    val organId : UUID,
    val itemName : String,
    val itemLogo : String?,
    val itemInfo : String,
    val createdAt : LocalDateTime?,
    val money : Int,
    val debt : Int,
    val capital : Int,
    val profit : Int,
    val profitOg : Int,
    val profitBenefit : Int,
    val netProfit : Int,
    val isDeleted : Boolean
)
