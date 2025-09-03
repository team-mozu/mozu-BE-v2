package team.mozu.dsm.adapter.out.item.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.entity.QItemJpaEntity.itemJpaEntity
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.adapter.out.organ.persistence.repository.OrganRepository
import team.mozu.dsm.application.exception.organ.OrganNotFoundException
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

@Component
class ItemPersistenceAdapter(
    private val itemRepository: ItemRepository,
    private val itemMapper: ItemMapper,
    private val organRepository: OrganRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : QueryItemPort, CommandItemPort {

    //--Query--//
    override fun existsById(id: UUID): Boolean {
        return itemRepository.existsById(id)
    }

    override fun findAllByIds(ids: List<UUID>): List<Item> {
        return itemRepository.findAllById(ids)
            .map { itemMapper.toModel(it) }
    }

    override fun findById(id: UUID): Item? {
        return itemRepository.findByIdOrNull(id)
            ?.let { itemMapper.toModel(it) }
    }

    //--Command--//
    override fun save(item: Item): Item {
        val organ = organRepository.findByIdOrNull(item.organId)
            ?: throw OrganNotFoundException

        val entity = itemMapper.toEntity(item, organ)
        val saved = itemRepository.save(entity)

        return itemMapper.toModel(saved)
    }

    override fun delete(item: Item) {
        jpaQueryFactory
            .update(itemJpaEntity)
            .set(itemJpaEntity.isDeleted, true)
            .where(itemJpaEntity.id.eq(item.id))
            .execute()
    }
}
