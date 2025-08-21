package team.mozu.dsm.domain.article.model

import team.mozu.dsm.domain.annotation.Aggregate
import java.time.LocalDateTime
import java.util.UUID

@Aggregate
data class Article (
    val id : UUID?,
    val organId : UUID,
    val articleName : String,
    val articleDescription : String,
    val articleImage : String?,
    val createAt : LocalDateTime?,
    val isDeleted : Boolean
)

