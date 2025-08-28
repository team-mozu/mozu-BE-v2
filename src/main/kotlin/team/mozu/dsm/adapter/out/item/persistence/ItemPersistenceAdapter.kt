package team.mozu.dsm.adapter.out.item.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.out.item.ItemQueryPort
import team.mozu.dsm.domain.item.model.Item
import java.util.*

@Component
class ItemPersistenceAdapter(
    private val itemRepository: ItemRepository,
    private val itemMapper: ItemMapper
) : ItemQueryPort {

    override fun findById(itemId: UUID): Item {
        return itemRepository.findById(itemId)
            .map { itemMapper.toModel(it) }
            .orElseThrow { ItemNotFoundException }
    }

    override fun findAllByIds(itemIds: List<UUID>): List<Item> {
        val items = itemRepository.findAllById(itemIds)
            .map { itemMapper.toModel(it) }

        val missingIds = itemIds - items.map { it.id }.toSet()
        if (missingIds.isNotEmpty()) {
            throw ItemNotFoundException
        }

        return items
    }
}
