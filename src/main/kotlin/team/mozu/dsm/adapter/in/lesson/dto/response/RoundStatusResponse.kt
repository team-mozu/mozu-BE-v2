package team.mozu.dsm.adapter.`in`.lesson.dto.response

import java.time.LocalDateTime
import java.util.UUID

data class RoundStatusResponse(
    val teamId: UUID,
    val teamName: String,
    val schoolName: String,
    val isInvestmentInProgress: Boolean,
    val currentRound: Int,
    val snapshots: List<RoundSnapshotEntry>
)

data class RoundSnapshotEntry(
    val round: Int,
    val totalMoney: Long,
    val cashMoney: Long,
    val valuationMoney: Long,
    val profitNum: Double,
    val endedAt: LocalDateTime
)
