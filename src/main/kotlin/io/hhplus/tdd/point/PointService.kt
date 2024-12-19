package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.port.PointHistoryPort
import io.hhplus.tdd.point.port.UserPointPort
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointPort: UserPointPort,
    private val pointHistoryPort: PointHistoryPort
) {
    fun getUserPoint(
        id: Long
    ): UserPoint {
        return userPointPort.getById(id = id)
    }

    fun getUserPointHistory(
        userId: Long,
    ): List<PointHistory> {
        return pointHistoryPort.getAllByUserId(userId = userId)
    }
}