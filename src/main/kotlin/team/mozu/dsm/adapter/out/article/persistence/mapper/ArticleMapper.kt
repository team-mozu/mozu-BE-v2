package team.mozu.dsm.adapter.out.article.persistence.mapper

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.article.model.Article

@Component
class ArticleMapper {

    fun toModel(entity: ArticleJpaEntity): Article {
        return Article(
            id = entity.id,
            organId = entity.organ.id!!,
            articleName =  entity.articleName,
            articleDescription = entity.articleDescription,
            articleImage = entity.articleImage,
            isDeleted = entity.isDeleted,
            createAt = entity.createdAt
        )
    }

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
