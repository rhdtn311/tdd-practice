package io.hhplus.tdd.point.model

data class PointHistory(
    val id: Long? = null,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,
    val timeMillis: Long,
) {
    companion object {
        fun createFromUserPoint(
            userPoint: UserPoint,
            type: TransactionType,
            amount: Long,
        ): PointHistory {
            return PointHistory(
                userId = userPoint.id,
                type = type,
                amount = amount,
                timeMillis = System.currentTimeMillis(),
            )
        }
    }
}

/**
 * 포인트 트랜잭션 종류
 * - CHARGE : 충전
 * - USE : 사용
 */
enum class TransactionType {
    CHARGE, USE
}