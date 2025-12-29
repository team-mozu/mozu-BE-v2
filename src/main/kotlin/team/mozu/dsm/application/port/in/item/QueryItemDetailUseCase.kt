package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse

interface QueryItemDetailUseCase {

    fun execute(id: Int): ItemResponse
}
