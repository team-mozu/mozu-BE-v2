package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.item.dto.response.ItemDetailResponse
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.item.QueryItemDetailUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort

@Service
class QueryItemDetailService(
    private val queryItemPort: QueryItemPort
) : QueryItemDetailUseCase {

    @Transactional(readOnly = true)
    override fun execute(id: Int): ItemDetailResponse =
        ItemDetailResponse.from(
            queryItemPort.findById(id)
                ?: throw ItemNotFoundException
        )
}
