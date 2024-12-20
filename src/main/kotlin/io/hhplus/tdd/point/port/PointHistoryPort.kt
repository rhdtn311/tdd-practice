package io.hhplus.tdd.point.port

import io.hhplus.tdd.point.model.PointHistory

interface PointHistoryPort {
    fun getAllByUserId(userId: Long): List<PointHistory>
    fun save(pointHistory: PointHistory): PointHistory
}