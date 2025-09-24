package team.mozu.dsm.adapter.out.article.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleQueryResponse
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.article.model.Article

@Mapper(componentModel = "spring")
abstract class ArticleMapper {

    @Mapping(target = "organId", source = "organ.id")
    @Mapping(target = "articleDesc", source = "articleDesc")
    abstract fun toModel(entity: ArticleJpaEntity): Article

    @Mapping(target = "articleDesc", source = "articleDesc")
    @Mapping(target = "articleImg", source = "articleImage")
    abstract fun toResponse(model: Article): ArticleResponse

    abstract fun toQueryResponse(model: Article): ArticleQueryResponse

    fun toEntity(model: Article, organ: OrganJpaEntity): ArticleJpaEntity {
        return ArticleJpaEntity(
            organ = organ,
            articleName = model.articleName,
            articleDesc = model.articleDesc,
            articleImage = model.articleImage,
            isDeleted = model.isDeleted
        )
    }
}
