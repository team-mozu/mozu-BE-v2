package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse

interface QueryItemsUseCase {

    fun execute(): List<ItemResponse>
}
