# Maintenance Notes

`build-logic`를 수정하거나 plugin 책임을 바꿀 때 인지해야 할 점과 검증 기준을 정리한 문서입니다.

## 인지해야 할 점

### 테스트 책임 분리

- `nevera.test.android`는 일반 Android 테스트만 담당
- Compose 테스트 의존성은 `nevera.android.compose`와 `nevera.android.application`이 담당

즉, Compose 모듈인데 `nevera.android.library`만 쓰면 Compose UI 테스트 의존성은 자동으로 들어오지 않습니다.

### `nevera.network`는 최소 공통화 성격

현재 `nevera.network`는 네트워크 의존성 중복 제거 목적의 plugin입니다.  
`retrofit`과 `converter-gson`을 포함하므로, 나중에 직렬화 전략을 변경할 때는 이 plugin부터 다시 조정해야 합니다.

### `nevera.firebase`의 범위

`nevera.firebase`는 Messaging만 담당합니다.

포함:

- Firebase BOM
- Firebase Messaging

포함하지 않음:

- `google-services`
- `firebase-crashlytics`

이 둘은 현재 `nevera.android.application` 쪽 책임입니다.

### `nevera.feature`는 이미지 로딩까지 포함

Feature 모듈은 별도 선언 없이 아래 구성을 공통으로 받습니다.

- `coil-compose`
- `coil-network-okhttp`

즉, 일반적인 네트워크 이미지 로드는 feature 모듈에서 바로 사용할 수 있습니다.

### build-logic 수정 시 확인할 것

plugin 책임을 바꾸면 실제 모듈 선언도 함께 검토해야 합니다.

대표 확인 대상:

- `:app`
- `:core:network`
- `:data`
- `:infra:notification`
- `feature:*`

## 검증 가이드

`build-logic` 수정 후에는 최소 아래 항목을 확인하는 것을 권장합니다.

- `./gradlew :build-logic:compileKotlin`
- 영향받는 대표 모듈의 `assemble` 또는 `test`
- 필요 시 `dependencies --configuration ...`로 의존성 해상도 확인

대표 예:

- `./gradlew :build-logic:compileKotlin`
- `./gradlew :domain:test`
- `./gradlew :data:assembleDebug`
- `./gradlew :infra:notification:testDebugUnitTest`
