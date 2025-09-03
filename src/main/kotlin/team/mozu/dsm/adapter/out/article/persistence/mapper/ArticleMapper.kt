package team.mozu.dsm.adapter.out.article.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.`in`.article.dto.response.ArticleResponse
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.article.model.Article

@Mapper(componentModel = "spring")
abstract class ArticleMapper {

    @Mapping(target = "organId", source = "organ.id")
    abstract fun toModel(entity: ArticleJpaEntity): Article
    @Mapping(target = "articleDesc", source = "articleDescription")
    @Mapping(target = "articleImg",  source = "articleImage", defaultValue = "")

    abstract fun toResponse(model: Article): ArticleResponse

    fun toEntity(model: Article, organ: OrganJpaEntity): ArticleJpaEntity {
        return ArticleJpaEntity(
            organ = organ,
            articleName = model.articleName,
            articleDescription = model.articleDescription,
            articleImage = model.articleImage,
            isDeleted = model.isDeleted
        )
    }
}
