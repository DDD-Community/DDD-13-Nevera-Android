# Plugin Catalog

`build-logic`에서 제공하는 Convention Plugin의 책임과 내부 조합 관계를 정리한 문서입니다.

## Plugin 목록

| Plugin ID | 역할 | 내부 조합 |
| --- | --- | --- |
| `nevera.test.unit` | JVM 단위 테스트 의존성 제공 | 없음 |
| `nevera.test.android` | Android 테스트 설정 + Android test 의존성 제공 | `nevera.test.unit` |
| `nevera.network` | 네트워크 공통 스택 제공 | 없음 |
| `nevera.firebase` | Firebase Messaging 공통 스택 제공 | 없음 |
| `nevera.kotlin.jvm` | 순수 Kotlin JVM 모듈 기본 구성 | `nevera.test.unit` |
| `nevera.android.library` | Android library 모듈 기본 구성 | `nevera.test.android` |
| `nevera.android.compose` | Compose Android library 구성 | `nevera.android.library` |
| `nevera.android.hilt` | Hilt + KSP 구성 | 없음 |
| `nevera.feature` | feature 모듈 공통 구성 | `nevera.android.compose`, `nevera.android.hilt` |
| `nevera.android.application` | app 모듈 공통 구성 | `nevera.android.hilt`, `nevera.test.android` |

## Plugin별 상세

### `nevera.test.unit`

공통 JUnit5 의존성을 제공합니다.

- `testImplementation(junit-jupiter)`
- `testRuntimeOnly(junit-jupiter-engine)`
- `testRuntimeOnly(junit-platform-launcher)`

직접 적용하는 경우는 거의 없고, 보통 `nevera.kotlin.jvm` 또는 `nevera.test.android`를 통해 간접 적용됩니다.

### `nevera.test.android`

Android 모듈의 공통 테스트 설정을 담당합니다.

- `nevera.test.unit` 내부 적용
- `com.android.library` 또는 `com.android.application` 적용 시 `useJUnitPlatform()` 설정
- `androidTestImplementation(androidx-junit)`
- `androidTestImplementation(espresso-core)`

Compose 테스트 의존성은 포함하지 않습니다. Compose 관련 테스트는 `nevera.android.compose` 또는 `nevera.android.application`이 담당합니다.

### `nevera.network`

네트워크 관련 공통 의존성을 제공합니다.

- `implementation(retrofit)`
- `implementation(retrofit-converter-gson)`
- `implementation(okhttp)`
- `implementation(okhttp-logging)`

현재 이 plugin은 “직렬화 전략 통일”보다 “중복 의존성 제거” 목적에 가깝습니다.

### `nevera.firebase`

Firebase Messaging 관련 공통 의존성을 제공합니다.

- `implementation(platform(firebase-bom))`
- `implementation(firebase-messaging)`

`google-services` plugin이나 `firebase-crashlytics`는 포함하지 않습니다.

### `nevera.kotlin.jvm`

Android 의존성이 없는 순수 Kotlin 모듈용 기본 plugin입니다.

- `org.jetbrains.kotlin.jvm` 적용
- `nevera.test.unit` 적용
- `kotlin { jvmToolchain(17) }`
- 모든 `Test` task에 `useJUnitPlatform()`

### `nevera.android.library`

일반 Android library 모듈의 기본 plugin입니다.

- `com.android.library` 적용
- `nevera.test.android` 적용
- `compileSdk = 36`
- `minSdk = 30`
- `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`
- Java 17 `compileOptions`

Compose, Hilt, 네트워크, Firebase는 포함하지 않습니다.

### `nevera.android.compose`

Compose UI를 사용하는 Android library 모듈용 plugin입니다.

- `nevera.android.library` 적용
- `org.jetbrains.kotlin.plugin.compose` 적용
- `buildFeatures { compose = true }`
- Compose BOM
- `compose-ui`, `material3`
- `debugImplementation(ui-tooling)`
- `debugImplementation(ui-test-manifest)`
- `androidTestImplementation(compose-ui-test-junit4)`

### `nevera.android.hilt`

Hilt DI가 필요한 모듈에서 조합해서 사용하는 plugin입니다.

- `com.google.dagger.hilt.android` 적용
- `com.google.devtools.ksp` 적용
- `implementation(hilt-android)`
- `ksp(hilt-compiler)`

보통 `nevera.android.library` 또는 `nevera.android.compose`와 함께 사용합니다.

### `nevera.feature`

Feature 화면 모듈 전용 조합 plugin입니다.

- `nevera.android.compose` 적용
- `nevera.android.hilt` 적용
- `implementation(project(":core:common"))`
- `implementation(project(":core:designsystem"))`
- `implementation(project(":core:ui"))`
- `implementation(project(":domain"))`
- `implementation(lifecycle-viewmodel-compose)`
- `implementation(hilt-navigation-compose)`
- `implementation(coil-compose)`
- `implementation(coil-network-okhttp)`
- `implementation(timber)`

### `nevera.android.application`

앱 진입점 모듈 전용 plugin입니다.

- `com.android.application` 적용
- `com.google.gms.google-services` 적용
- `com.google.firebase.crashlytics` 적용
- `org.jetbrains.kotlin.plugin.compose` 적용
- `nevera.android.hilt` 적용
- `nevera.test.android` 적용
- `minSdk = 30`
- `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`
- Java 17 `compileOptions`
- `buildFeatures { compose = true; buildConfig = true }`
- Compose BOM
- `debugImplementation(ui-tooling)`
- `debugImplementation(ui-test-manifest)`
- `androidTestImplementation(compose-ui-test-junit4)`

현재 `:app` 전용입니다.
