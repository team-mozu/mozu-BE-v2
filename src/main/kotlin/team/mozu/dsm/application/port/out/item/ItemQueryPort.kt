package team.mozu.dsm.application.port.out.item

import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

interface ItemQueryPort {

    fun findAllByIds(itemIds: List<UUID>): List<Item>
}
