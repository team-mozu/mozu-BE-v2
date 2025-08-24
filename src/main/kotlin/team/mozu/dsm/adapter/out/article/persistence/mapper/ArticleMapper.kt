package team.mozu.dsm.adapter.out.article.persistence.mapper

import org.mapstruct.Mapper
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.article.model.Article

@Mapper(componentModel = "spring")
abstract class ArticleMapper {

    abstract fun toModel(entity: ArticleJpaEntity): Article

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
