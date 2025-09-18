package team.mozu.dsm.application.service.item

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.application.exception.item.ItemNotFoundException
import team.mozu.dsm.application.exception.lesson.CannotDeleteLessonException
import team.mozu.dsm.application.port.`in`.item.DeleteItemUseCase
import team.mozu.dsm.application.port.out.auth.SecurityPort
import team.mozu.dsm.application.port.out.item.CommandItemPort
import team.mozu.dsm.application.port.out.item.QueryItemPort
import java.util.*

@Service
class DeleteItemService(
    private val queryItemPort: QueryItemPort,
    private val commandItemPort: CommandItemPort,
    private val securityPort: SecurityPort
) : DeleteItemUseCase {

    @Transactional
    override fun delete(id: UUID) {
        val organ = securityPort.getCurrentOrgan()
        val item = queryItemPort.findById(id)
            ?: throw ItemNotFoundException

        if (item.organId != organ.id) {
            throw CannotDeleteLessonException
        }

        commandItemPort.delete(item)
    }
}
