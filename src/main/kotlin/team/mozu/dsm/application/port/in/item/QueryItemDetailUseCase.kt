package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.domain.item.model.Item

interface QueryItemDetailUseCase {

    fun queryDetail(id: Int): Item
}
