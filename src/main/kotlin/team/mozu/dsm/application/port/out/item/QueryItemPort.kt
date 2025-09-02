package team.mozu.dsm.application.port.out.item

import team.mozu.dsm.domain.item.model.Item
import java.util.UUID

interface QueryItemPort {

    fun existsById(id: UUID): Boolean

    fun findAllByIds(ids: List<UUID>): List<Item>

    fun findById(id: UUID): Item?

}
