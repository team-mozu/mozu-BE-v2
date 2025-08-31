package team.mozu.dsm.application.port.out.item

import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

interface QueryItemPort {

    fun findAllByIds(itemIds: List<UUID>): List<Item>
}
