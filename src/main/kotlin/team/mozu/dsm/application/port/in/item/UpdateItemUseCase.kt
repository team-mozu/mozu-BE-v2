package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.request.ItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse

interface UpdateItemUseCase {

    fun update(id: Int, request: ItemRequest): ItemResponse
}
