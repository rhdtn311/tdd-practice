package io.hhplus.tdd.point.port

import io.hhplus.tdd.point.model.UserPoint

interface UserPointPort {
    fun getById(id: Long): UserPoint
}