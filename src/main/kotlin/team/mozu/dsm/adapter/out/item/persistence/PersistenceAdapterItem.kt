package team.mozu.dsm.adapter.out.item.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.domain.item.model.Item
import java.util.*

@Component
class PersistenceAdapterItem(
    private val itemRepository: ItemRepository,
    private val itemMapper: ItemMapper
) : QueryItemPort {

    override fun findAllByIds(itemIds: List<UUID>): List<Item> {
        return itemRepository.findAllById(itemIds)
            .map { itemMapper.toModel(it) }
    }
}
