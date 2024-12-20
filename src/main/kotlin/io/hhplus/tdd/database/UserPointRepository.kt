package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.port.UserPointPort
import org.springframework.stereotype.Component

@Component
class UserPointRepository(
    private val userPointTable: UserPointTable,
): UserPointPort {
    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(
            id = id,
        )
    }

    override fun save(userPoint: UserPoint): UserPoint {
        return userPointTable.insertOrUpdate(
            id = userPoint.id,
            amount = userPoint.point,
        )
    }
}