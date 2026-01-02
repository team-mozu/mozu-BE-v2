package team.mozu.dsm.application.port.out.item

import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

interface QueryItemPort {

    fun findAllByIds(ids: Set<Int>): List<Item>

    fun findById(id: Int): Item?

    fun findAllByOrganId(organId: UUID): List<Item>
}
