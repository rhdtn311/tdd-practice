package io.hhplus.tdd.point.model

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long? = null,
) {
    companion object {
        const val MIN_POINT = 0L
        const val MAX_POINT = 1000L
    }

    init {
        require(point in MIN_POINT.. MAX_POINT) { "포인트는 0보다 적거나 1000보다 많을 수 없습니다." }
    }

    fun charge(
        chargingPoint: Long,
    ): UserPoint {
        require(chargingPoint > 0) {"충전하려는 포인트는 0보다 커야합니다."}
        require (this.point + chargingPoint <= MAX_POINT) { "총 포인트는 1000보다 많을 수 없습니다." }

        return this.copy(
            point = this.point + chargingPoint
        )
    }

    fun use(usingPoint: Long): UserPoint {
        require(usingPoint > 0) { "사용하려는 포인트는 0보다 커야 합니다." }
        require(this.isMoreThen(usingPoint)) { "사용하려는 포인트가 잔여 포인트보다 클 수 없습니다." }

        return this.copy(
            point = this.point - usingPoint
        )
    }

    private fun isMoreThen(point: Long): Boolean {
        return this.point >= point
    }
}
