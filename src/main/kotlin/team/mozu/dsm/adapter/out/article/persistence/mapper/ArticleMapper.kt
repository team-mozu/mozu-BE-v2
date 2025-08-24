package team.mozu.dsm.adapter.out.article.persistence.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import team.mozu.dsm.adapter.out.article.entity.ArticleJpaEntity
import team.mozu.dsm.adapter.out.organ.entity.OrganJpaEntity
import team.mozu.dsm.domain.article.model.Article

@Mapper(componentModel = "spring")
interface ArticleMapper {

    @Mapping(target = "organId", source = "entity.organ.id")
    fun toModel(entity: ArticleJpaEntity): Article

    @Mapping(target = "id", source = "model.id")
    fun toEntity(model: Article, organ: OrganJpaEntity): ArticleJpaEntity
}
