package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.port.PointHistoryPort
import io.hhplus.tdd.point.port.UserPointPort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PointServiceTest {

    @Mock
    lateinit var userPointPort: UserPointPort

    @Mock
    lateinit var pointHistoryPort: PointHistoryPort

    @InjectMocks
    lateinit var pointService: PointService

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    @DisplayName("포인트 조회")
    inner class GetPoint {
        @Test
        @DisplayName("Success - 유저 ID로 유저포인트 조회에 성공한다.")
        fun getUserByIdSuccess() {
            // given
            val id = 1L
            val userPointOfUserId1 = UserPointDummies.successUserPoint
            `when`(userPointPort.getById(id)).thenReturn(userPointOfUserId1)

            // when
            val resultUserPoint = pointService.getUserPoint(id = id)

            // then
            Assertions.assertThat(resultUserPoint).isEqualTo(userPointOfUserId1)
        }
    }

    @Nested
    @DisplayName("포인트 기록 조회")
    inner class GetPointHistory {
        @Test
        @DisplayName("Success - 유저 ID로 유저포인트 기록 조회에 성공한다.")
        fun getUserPointHistorySuccess() {
            // given
            val userId = 1L
            val pointHistoriesOfUserId1 = UserPointDummies.successPointHistories.filter {
                pointHistory -> pointHistory.userId == userId
            }
            `when`(pointHistoryPort.getAllByUserId(userId)).thenReturn(pointHistoriesOfUserId1)

            // when
            val resultPointHistories = pointService.getUserPointHistory(userId = userId)

            // then
            Assertions.assertThat(resultPointHistories).isEqualTo(pointHistoriesOfUserId1)
        }
    }
}