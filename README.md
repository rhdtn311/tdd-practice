## 1. 문제 상황

동일 사용자의 포인트에 대한 동시 충전 및 사용 요청 시 데이터 정합성이 깨지는 현상이 발생했습니다.

(충전과 사용 시나리오 자체는 같기 때문에 ‘충전’인 경우로만 가정하여 설명합니다.)

### 1.1 시나리오 상세
![image](https://github.com/user-attachments/assets/0974ff23-5d73-47b6-87cd-76f8763352c5)

초기 상태: ID 1번 사용자의 포인트 = 500

**요청 상황**

- 사용자 A: ID 1번 계정에 10 포인트 충전 요청
- 사용자 B: ID 1번 계정에 10 포인트 충전 요청

**실제 진행 순서**

1. [사용자 A] 포인트 조회
    - 조회된 포인트: 500
2. [사용자 B] 포인트 조회
    - 조회된 포인트: 500
3. [사용자 A] 포인트 계산 및 수정
    - 계산: 500 + 10 = 510
4. [사용자 B] 포인트 계산 및 수정
    - 계산: 500 + 10 = 510
5. [사용자 A] DB 저장
    - 저장된 값: 510
6. [사용자 B] DB 저장
    - 저장된 값: 510

### 1.2 문제점

- 기대 결과: 520 포인트 (500 + 10 + 10)
- 실제 결과: 510 포인트
- 문제 원인: Lost Update 현상 발생
    - 두 트랜잭션이 동일한 초기값(500)을 기준으로 각각 업데이트를 수행
    - 후순위 업데이트가 선순위 업데이트의 결과를 덮어씀

### 1.3 문제 확인 테스트 코드

```kotlin
@Test
fun concurrencyTest() {
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
    Assertions.assertEquals(result, expectedPoint)
}
```

- 총 10개의 쓰레드를 생성하여 ID가 1인 유저포인트에 대해 1 포인트씩 충전하는 요청을 100번 동시 실행
- 기대값 : 500(초기포인트) + 100 * 1 = 600
- 결과값 : 534

![image](https://github.com/user-attachments/assets/aad2fd13-6ad3-4b16-8b8c-e10fbc341dfb)

## 2. 영향도

1. 데이터 정합성 훼손
    - 사용자의 포인트가 정상적으로 반영되지 않음
    - 포인트의 손실 발생
2. 사용자 경험 저하
    - 정상적으로 충전했음에도 포인트가 누락되는 현상 발생

## 3. 기술적 원인

현재 시스템의 동시성 제어 메커니즘 부재

## 4. 개선 방향과 추후 고려 사항

### 5.1 개선 방향 :`@Synchronized` 키워드 활용

```kotlin
@Synchronized  // 추가
fun chargePoint(
    id: Long,
    chargingPoint: Long
): UserPoint {
   ... 
}

@Synchronized  // 추가
fun usePoint(
    id: Long,
    usingPoint: Long
): UserPoint {
   ...
}
```

- `@Synchronized`를 사용하여 `chargePoint` 메서드에 대해 동시성 제어를 추가
- 동일 객체 내 메서드 호출 시 하나의 쓰레드만 접근 가능하도록 보장

### 장점

- 간단한 구현으로 동시성 문제를 해결할 수 있음

### 단점

- `@Synchronized`는 애플리케이션 수준에서 동작하며, 분산 환경에서는 효과가 없음

## 5. 개선 결과

### 5.1 테스트 코드

```kotlin
@Test
@DisplayName("Success - 동시 충전 요청에도 유저포인트가 기대한 값으로 정확히 반영된다.")
fun chargeUserPointSuccessWithConcurrency() {
    // given
    val userId = 1L
    val initialPoint = 500L
    val concurrencyRequestSize = 100
    val chargingPoint = 1L
    val expectedPoint = initialPoint + (concurrencyRequestSize * chargingPoint)

    userPointRepository.save(
        UserPointDummies.successUserPoint.copy(point = initialPoint)
    )

    val executor = Executors.newFixedThreadPool(10)
    val latch = CountDownLatch(concurrencyRequestSize)

    // when
    repeat(concurrencyRequestSize) {
        executor.submit {
            try {
                pointService.chargePoint(userId, chargingPoint) // 포인트 충전 요청
            } finally {
                latch.countDown() // 요청 완료 시 카운트 감소
            }
        }
    }

    latch.await()
    executor.shutdown()

    val result = userPointRepository.getById(id = userId).point
    Assertions.assertEquals(result, expectedPoint)
}

```

---

### 5.2 테스트 결과
![image](https://github.com/user-attachments/assets/7718d44e-15dc-4e19-be53-6e4f568c7c87)

- **초기 상태**
    - 유저 포인트: 500
- **충전 요청**
    - 100개의 요청이 동시에 발생, 요청당 1 포인트 충전
- **기대 결과**
    - 최종 유저 포인트: 600 (500 + 100)
- **실제 결과**
    - 최종 유저 포인트: 600

### 5.3 개선점

### 문제 해결

- `@Synchronized`를 활용하여 `chargePoint` 메서드에서 동시성 문제가 발생하지 않도록 제어
- 모든 충전 요청이 안전하게 처리되며, Lost Update 현상이 완전히 제거됨

### 코드 변경 사항

- 동시성 제어를 위해 `@Synchronized` 키워드를 추가
- 동시성 테스트를 통해 100개의 충전 요청이 성공적으로 처리됨을 확인
