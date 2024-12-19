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
}
