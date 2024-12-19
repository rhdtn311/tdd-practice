package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.UserPoint

class UserPointDummies {
    companion object {
        val successUserPoint = UserPoint(
            id = 1L,
            point = 100L,
            updateMillis = 500L,
        )
    }
}
