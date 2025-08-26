package team.mozu.dsm.application.port.`in`.team

import team.mozu.dsm.application.port.`in`.team.dto.request.TeamParticipationCommand
import team.mozu.dsm.application.port.`in`.team.dto.response.TeamToken

/**
 * 외부 요청 DTO에 의존하지 않고 포트 전용 모델을 사용함으로써
 * UseCase가 어댑터 계층에 종속되지 않도록 설계
 */
interface TeamParticipationUseCase {

    fun participate(command: TeamParticipationCommand): TeamToken
}
