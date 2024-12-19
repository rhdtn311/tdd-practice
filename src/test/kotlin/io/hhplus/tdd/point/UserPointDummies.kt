package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint

class UserPointDummies {
    companion object {
        val successUserPoint = UserPoint(
            id = 1L,
            point = 100L,
            updateMillis = 500L,
        )

        val successPointHistory = PointHistory(
            id = 1L,
            userId = 1L,
            type = TransactionType.USE,
            amount = 100L,
            timeMillis = 500L,
        )

        val successPointHistories = listOf(
            successPointHistory.copy(id = 1L, userId = 1L, type = TransactionType.USE, amount = 100L, timeMillis = 500L),
            successPointHistory.copy(id = 2L, userId = 1L, type = TransactionType.USE, amount = 200L, timeMillis = 500L),
            successPointHistory.copy(id = 3L, userId = 1L, type = TransactionType.USE, amount = 300L, timeMillis = 500L),
            successPointHistory.copy(id = 4L, userId = 2L, type = TransactionType.CHARGE, amount = 400L, timeMillis = 500L),
            successPointHistory.copy(id = 5L, userId = 2L, type = TransactionType.CHARGE, amount = 500L, timeMillis = 500L)
        )
    }
}
