package team.mozu.dsm.adapter.out.sse

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import team.mozu.dsm.application.port.out.lesson.QueryLessonPort
import team.mozu.dsm.application.port.out.sse.SseAuthenticationService
import team.mozu.dsm.application.port.out.team.QueryTeamPort
import team.mozu.dsm.global.security.auth.CustomUserDetails
import team.mozu.dsm.global.security.auth.StudentPrincipal
import team.mozu.dsm.global.security.jwt.JwtAdapter
import team.mozu.dsm.global.exception.ExpiredTokenException
import team.mozu.dsm.global.exception.InvalidTokenException
import java.util.UUID

@Service
class SseAuthenticationServiceImpl(
    private val jwtAdapter: JwtAdapter,
    private val queryTeamPort: QueryTeamPort,
    private val queryLessonPort: QueryLessonPort
) : SseAuthenticationService {

    private val logger = LoggerFactory.getLogger(SseAuthenticationServiceImpl::class.java)

    companion object {
        private const val TYPE_CLAIM = "type"
        private const val STUDENT_ACCESS_TYPE = "student_access"
        private const val ACCESS_TYPE = "access"
        private const val TEAM_ID_CLAIM = "teamId"
    }

    override fun validateToken(token: String): StudentPrincipal? {
        return try {
            val claims = jwtAdapter.getClaims(token)
            val tokenType = claims.get(TYPE_CLAIM, String::class.java)

            if (tokenType != STUDENT_ACCESS_TYPE) {
                logger.warn("Invalid token type for SSE connection: {}", tokenType)
                return null
            }

            val lessonNum = claims.subject
            val teamIdString = claims.get(TEAM_ID_CLAIM, String::class.java)
            val teamId = UUID.fromString(teamIdString)

            StudentPrincipal(lessonNum, teamId)
        } catch (e: ExpiredTokenException) {
            logger.warn("Expired token used for SSE connection")
            null
        } catch (e: InvalidTokenException) {
            logger.warn("Invalid token used for SSE connection")
            null
        } catch (e: Exception) {
            logger.error("Error validating SSE token", e)
            null
        }
    }

    override fun validateTeamAccess(principal: StudentPrincipal, teamId: UUID): Boolean {
        // 1. 기본 팀 ID 매칭 검증
        if (principal.teamId != teamId) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Team ID mismatch - user belongs to team ${principal.teamId} but tried to access team $teamId"
            )
            return false
        }

        // 2. 팀이 실제로 존재하는지 검증
        val team = try {
            queryTeamPort.findById(teamId)
        } catch (e: Exception) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Team not found: $teamId"
            )
            return false
        }

        // 3. 수업 번호 매칭 검증
        if (team.lessonNum != principal.lessonNum) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Lesson number mismatch - user lesson: ${principal.lessonNum}, team lesson: ${team.lessonNum}"
            )
            return false
        }

        // 4. 수업이 활성 상태인지 검증
        val lesson = try {
            queryLessonPort.findById(team.lessonId)
        } catch (e: Exception) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Lesson not found: ${team.lessonId}"
            )
            return false
        }

        if (lesson == null) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Lesson not found: ${team.lessonId}"
            )
            return false
        }

        // 5. 수업이 삭제되지 않았는지 검증
        if (lesson.isDeleted) {
            logUnauthorizedAccess(
                principal = principal,
                resourceType = "TEAM_SSE",
                resourceId = teamId.toString(),
                reason = "Lesson is deleted: ${lesson.id}"
            )
            return false
        }

        return true
    }

    override fun validateAdminAccess(principal: StudentPrincipal, lessonId: UUID): Boolean {
        // StudentPrincipal은 관리자 권한을 가질 수 없음
        logUnauthorizedAccess(
            principal = principal,
            resourceType = "ADMIN_SSE",
            resourceId = lessonId.toString(),
            reason = "Student principal cannot access admin resources"
        )
        return false
    }

    /**
     * 관리자 인증을 위한 별도 메서드
     */
    fun validateAdminAuthentication(authentication: Authentication, lessonId: UUID): Boolean {
        // 1. 인증 객체 검증
        if (!authentication.isAuthenticated) {
            logger.warn("Unauthenticated user tried to access admin SSE for lesson: {}", lessonId)
            return false
        }

        // 2. 관리자 권한 검증
        val hasAdminRole = authentication.authorities?.any { it.authority == "ROLE_ORGAN" } ?: false

        if (!hasAdminRole) {
            logUnauthorizedAccess(
                principal = null,
                resourceType = "ADMIN_SSE",
                resourceId = lessonId.toString(),
                reason = "User does not have ROLE_ORGAN authority"
            )
            return false
        }

        // 3. 수업 존재 여부 검증
        val lesson = try {
            queryLessonPort.findById(lessonId)
        } catch (e: Exception) {
            logUnauthorizedAccess(
                principal = null,
                resourceType = "ADMIN_SSE",
                resourceId = lessonId.toString(),
                reason = "Lesson not found: $lessonId"
            )
            return false
        }

        if (lesson == null) {
            logUnauthorizedAccess(
                principal = null,
                resourceType = "ADMIN_SSE",
                resourceId = lessonId.toString(),
                reason = "Lesson not found: $lessonId"
            )
            return false
        }

        // 4. 수업이 삭제되지 않았는지 검증
        if (lesson.isDeleted) {
            logUnauthorizedAccess(
                principal = null,
                resourceType = "ADMIN_SSE",
                resourceId = lessonId.toString(),
                reason = "Lesson is deleted: $lessonId"
            )
            return false
        }

        // 5. 관리자가 해당 수업에 접근 권한이 있는지 검증 (기관 코드 매칭)
        if (authentication.principal is CustomUserDetails) {
            val organDetails = authentication.principal as CustomUserDetails
            val organCode = organDetails.organ.organName

            // 수업의 기관 코드와 관리자의 기관 코드가 일치하는지 확인
            if (lesson.organId != organDetails.organ.id) {
                logUnauthorizedAccess(
                    principal = null,
                    resourceType = "ADMIN_SSE",
                    resourceId = lessonId.toString(),
                    reason = "Organ mismatch - admin organ: ${organDetails.organ.id}, lesson organ: ${lesson.organId}"
                )
                return false
            }
        }

        return true
    }

    override fun isTokenExpired(token: String): Boolean {
        return try {
            jwtAdapter.getClaims(token)
            false
        } catch (e: ExpiredTokenException) {
            true
        } catch (e: Exception) {
            true
        }
    }

    override fun logUnauthorizedAccess(
        principal: StudentPrincipal?,
        resourceType: String,
        resourceId: String,
        reason: String
    ) {
        val userInfo = principal?.let { "lessonNum=${it.lessonNum}, teamId=${it.teamId}" } ?: "anonymous"
        logger.warn(
            "Unauthorized SSE access attempt - User: [{}], Resource: {}:{}, Reason: {}",
            userInfo,
            resourceType,
            resourceId,
            reason
        )

        // 보안 이벤트 로깅을 위한 추가 정보
        logger.info(
            "Security Event - Type: UNAUTHORIZED_ACCESS, Resource: {}, ResourceId: {}, User: {}, Reason: {}, Timestamp: {}",
            resourceType,
            resourceId,
            userInfo,
            reason,
            System.currentTimeMillis()
        )
    }
}
