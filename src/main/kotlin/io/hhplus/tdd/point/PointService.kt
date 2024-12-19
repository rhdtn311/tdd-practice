package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.port.UserPointPort
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointPort: UserPointPort,
) {
    fun getUserPoint(
        id: Long
    ): UserPoint {
        return userPointPort.getById(id = id)
    }
}