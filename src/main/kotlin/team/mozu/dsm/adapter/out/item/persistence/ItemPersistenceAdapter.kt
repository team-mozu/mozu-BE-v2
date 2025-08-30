package team.mozu.dsm.adapter.out.item.persistence

import org.springframework.stereotype.Component
import team.mozu.dsm.adapter.out.item.persistence.mapper.ItemMapper
import team.mozu.dsm.adapter.out.item.persistence.repository.ItemRepository
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

@Component
class ItemPersistenceAdapter(
    private val itemRepository: ItemRepository,
    private val itemMapper: ItemMapper
) : QueryItemPort {

    //--Query--//
    override fun existsById(id: UUID): Boolean {
        return itemRepository.existsById(id)
    }

    override fun findAllByIds(ids: Set<UUID>): List<Item> {
        return itemRepository.findAllById(ids)
            .map { itemMapper.toModel(it) }
    }

    //--Command--//
}
