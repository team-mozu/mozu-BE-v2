package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.item.QueryItemDetailUseCase
import team.mozu.dsm.application.port.out.item.QueryItemPort
import team.mozu.dsm.domain.item.model.Item
import java.util.*

@Service
class QueryItemDetailService(
    private val queryItemPort: QueryItemPort
) : QueryItemDetailUseCase {

    @Transactional(readOnly = true)
    override fun queryDetail(id: UUID): Item {
        return queryItemPort.findById(id)
            ?: throw ItemNotFoundException
    }
}
