package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.port.`in`.item.DeleteItemUseCase
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import java.util.*

@Service
class DeleteItemService (
    private val queryItemPort: QueryItemPort,
    private val commandItemPort: CommandItemPort
) : DeleteItemUseCase {

    @Transactional
    override fun delete(id: UUID) {
        val item = queryItemPort.findById(id)
            ?: throw ItemNotFoundException

        commandItemPort.delete(item)
    }
}
