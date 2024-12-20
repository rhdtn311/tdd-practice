package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.port.PointHistoryPort
import org.springframework.stereotype.Component

@Component
class PointHistoryRepository(
    private val pointHistoryTable: PointHistoryTable,
): PointHistoryPort {
    override fun getAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId = userId)
    }

    override fun save(pointHistory: PointHistory): PointHistory {
        return pointHistoryTable.insert(
            id = pointHistory.userId,
            amount = pointHistory.amount,
            transactionType = pointHistory.type,
            updateMillis = pointHistory.timeMillis,
        )
    }
}