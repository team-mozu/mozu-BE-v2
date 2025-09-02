package team.mozu.dsm.application.port.`in`.item

import team.mozu.dsm.adapter.`in`.item.dto.response.ItemResponse
import java.util.UUID

interface QueryItemDetailUseCase {
    fun queryDetail(id :UUID) : ItemResponse

}
