package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse

interface QueryItemAllUseCase {

    fun queryAll(): List<ItemResponse>
}
