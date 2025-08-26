package team.mozu.dsm.application.service.organ

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.mozu.dsm.adapter.`in`.organ.dto.request.CreateOrganRequest
import team.mozu.dsm.application.port.`in`.organ.CreateOrganUseCase
import team.mozu.dsm.application.port.out.organ.SaveOrganPort
import team.mozu.dsm.domain.organ.model.Organ

@Service
class CreateOrganService(
    private val saveOrganPort: SaveOrganPort,
    private val passwordEncoder: PasswordEncoder
) : CreateOrganUseCase {

    @Transactional
    override fun create(command: CreateOrganRequest): Organ {
        val organ = Organ(
            organCode = command.organCode,
            organName = command.organName,
            password = passwordEncoder.encode(command.password)
        )

        return saveOrganPort.save(organ)
    }
}
