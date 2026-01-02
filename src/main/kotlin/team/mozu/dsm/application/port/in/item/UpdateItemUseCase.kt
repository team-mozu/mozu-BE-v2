package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.request.UpdateItemRequest
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemDetailResponse

interface UpdateItemUseCase {

    fun execute(id: Int, request: UpdateItemRequest): ItemDetailResponse
}
