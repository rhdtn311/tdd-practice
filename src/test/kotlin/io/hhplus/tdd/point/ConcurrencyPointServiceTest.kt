package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ConcurrencyPointServiceTest {
    @Autowired
    private lateinit var pointService: PointService

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    @Nested
    @DisplayName("동시성을 고려한 테스트")
    inner class PointWithConcurrency {
        @Test
        @DisplayName("Success - 같은 유저포인트에 대한 100번의 포인트 추가 요청이 동시에 발생해도 기대하는 결과값으로 유저포인트 충전에 성공한다.")
        fun chargeUserPointSuccessWithConcurrency() {
            // given
            val userId = 1L
            val initialPoint = 500L
            val concurrencyRequestSize = 100
            val chargingPoint = 1L
            val expectedPoint = initialPoint + (concurrencyRequestSize * chargingPoint)
            userPointRepository.save(UserPointDummies.successUserPoint.copy(point = initialPoint))

            val executor = Executors.newFixedThreadPool(10)
            val latch = CountDownLatch(concurrencyRequestSize)

            // when
            repeat(100) {
                executor.submit {
                    try {
                        pointService.chargePoint(userId, chargingPoint)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            // then
            val result = userPointRepository.getById(id = userId)
                .point
            Assertions.assertThat(result).isEqualTo(expectedPoint)
        }

        @Test
        @DisplayName("Success - 같은 유저포인트에 대한 100번의 포인트 사용 요청이 동시에 발생해도 기대하는 결과값으로 유저포인트 사용에 성공한다.")
        fun useUserPointSuccessWithConcurrency() {
            // given
            val userId = 1L
            val initialPoint = 500L
            val concurrencyRequestSize = 100
            val usingPoint = 1L
            val expectedPoint = initialPoint - (concurrencyRequestSize * usingPoint)
            userPointRepository.save(UserPointDummies.successUserPoint.copy(point = initialPoint))

            val executor = Executors.newFixedThreadPool(10)
            val latch = CountDownLatch(concurrencyRequestSize)

            // when
            repeat(100) {
                executor.submit {
                    try {
                        pointService.usePoint(userId, usingPoint)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            // then
            val result = userPointRepository.getById(id = userId)
                .point
            Assertions.assertThat(result).isEqualTo(expectedPoint)
        }
    }
}