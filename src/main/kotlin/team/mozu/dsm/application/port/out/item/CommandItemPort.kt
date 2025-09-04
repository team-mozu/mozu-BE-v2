package team.mozu.dsm.application.port.out.item

import team.mozu.dsm.domain.item.model.Item

interface CommandItemPort {
    fun save(item: Item): Item

    fun delete(item: Item)
}
