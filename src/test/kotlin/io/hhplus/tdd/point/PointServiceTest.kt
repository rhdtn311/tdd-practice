package io.hhplus.tdd.point

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.port.PointHistoryPort
import io.hhplus.tdd.point.port.UserPointPort
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @Nested
    @DisplayName("포인트 충전")
    inner class ChargeUserPoint {
        @ParameterizedTest
        @ValueSource(longs = [1L, 900L])
        @DisplayName("Success - 유저포인트 충전에 성공한다.")
        fun chargeUserPointSuccess(chargingPoint: Long) {
            // given
            val initPoint = 100L
            val userPointHaving100Point = UserPointDummies.successUserPoint.copy(point = initPoint)
            val chargedUserPoint =
                UserPointDummies.successUserPoint.copy(point = userPointHaving100Point.point + chargingPoint)

            `when`(userPointPort.getById(userPointHaving100Point.id)).thenReturn(userPointHaving100Point)
            `when`(userPointPort.save(chargedUserPoint)).thenReturn(chargedUserPoint)

            // when
            val result = pointService.chargePoint(
                id = userPointHaving100Point.id,
                chargingPoint = chargingPoint
            )

            // then
            Assertions.assertThat(result).isEqualTo(chargedUserPoint)
        }

        @ParameterizedTest
        @ValueSource(longs = [0L, -1L])
        @DisplayName("Fail - 충전 히려는 포인트가 0보다 작거나 같으면 예외를 발생시킨다.")
        fun chargeUserPointUnderAndEqualZeroPointFail(chargingPoint: Long) {
            // given
            val initUserPoint = UserPointDummies.successUserPoint

            `when`(userPointPort.getById(initUserPoint.id)).thenReturn(initUserPoint)

            // when & then
            assertThatThrownBy {
                pointService.chargePoint(id = initUserPoint.id, chargingPoint = chargingPoint)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("충전하려는 포인트는 0보다 커야합니다.")
        }

        @ParameterizedTest
        @ValueSource(longs = [51L, 52L])
        @DisplayName("Fail - 충전 후 총 포인트가 최대 포인트를 초과하면 예외를 발생시킨다.")
        fun chargeUserPointExceedMaxPointFail(chargingPoint: Long) {
            // given
            val initPoint = 950L
            val userPointHaving950Point = UserPointDummies.successUserPoint
                .copy(point = initPoint)

            `when`(userPointPort.getById(userPointHaving950Point.id)).thenReturn(userPointHaving950Point)

            // when & then
            assertThatThrownBy {
                pointService.chargePoint(id = userPointHaving950Point.id, chargingPoint = chargingPoint)
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("총 포인트는 1000보다 많을 수 없습니다.")
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    inner class UseUserPoint {
        @ParameterizedTest
        @ValueSource(longs = [299L, 300L])
        @DisplayName("Success - 유저포인트 사용에 성공한다.")
        fun useUserPointSuccess(usingPoint: Long) {
            // given
            val initPoint = 300L
            val userPointHaving300Point = UserPointDummies.successUserPoint.copy(point = initPoint)
            val usedUserPoint = UserPointDummies.successUserPoint.copy(
                point = userPointHaving300Point.point - usingPoint
            )

            `when`(userPointPort.getById(userPointHaving300Point.id)).thenReturn(userPointHaving300Point)
            `when`(userPointPort.save(usedUserPoint)).thenReturn(usedUserPoint)

            // when
            val result = pointService.usePoint(
                id = userPointHaving300Point.id,
                usingPoint = usingPoint
            )

            // then
            Assertions.assertThat(result).isEqualTo(usedUserPoint)
        }

        @ParameterizedTest
        @ValueSource(longs = [301L, 500L])
        @DisplayName("Fail - 사용하려는 포인트가 잔여 포인트보다 많으면 예외를 발생시킨다.")
        fun useUserPointExceedAvailablePointFail(usingPoint: Long) {
            // given
            val initPoint = 300L
            val userPointHaving300Point = UserPointDummies.successUserPoint.copy(point = initPoint)

            `when`(userPointPort.getById(userPointHaving300Point.id)).thenReturn(userPointHaving300Point)

            // when & then
            assertThatThrownBy {
                pointService.usePoint(
                    id = userPointHaving300Point.id,
                    usingPoint = usingPoint
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("사용하려는 포인트가 잔여 포인트보다 클 수 없습니다.")
        }

        @ParameterizedTest
        @ValueSource(longs = [0L, -1L])
        @DisplayName("Fail - 사용하려는 포인트가 0보다 작거나 같으면 예외를 발생시킨다.")
        fun useUserPointUnderAndEqualZeroPointFail(usingPoint: Long) {
            // given
            val initUserPoint = UserPointDummies.successUserPoint

            `when`(userPointPort.getById(initUserPoint.id)).thenReturn(initUserPoint)

            // when & then
            assertThatThrownBy {
                pointService.usePoint(
                    id = initUserPoint.id,
                    usingPoint = usingPoint
                )
            }.isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("사용하려는 포인트는 0보다 커야 합니다.")
        }
    }

}