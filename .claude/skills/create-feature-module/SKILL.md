---
name: create-feature-module
description: |
  feature 모듈 껍데기를 MVI 구조로 생성한다. "feature 모듈 만들어줘", "feature 껍데기 생성", "create-feature-module" 등의 요청에 응답한다.
  build.gradle.kts, Intent/Screen/ViewModel/UiState/SideEffect/Navigation/Content 파일 생성,
  settings.gradle.kts 및 app/build.gradle.kts 자동 등록까지 한 번에 처리한다.
  $ARGUMENTS로 모듈명(소문자, 예: profile / user-profile)을 받는다.
argument-hint: <모듈명 (예: profile, user-profile)>
version: 0.2.0
---

# create-feature-module

## 이름 변환 규칙

### 입력 정규화

`$ARGUMENTS`가 어떤 형태로 오더라도 단어 목록으로 먼저 분리한다.

| 입력 예시 | 단어 분리 결과 |
|---|---|
| `mypage` | `[mypage]` |
| `myPage` | `[my, page]` |
| `MyPage` | `[my, page]` |
| `my-page` | `[my, page]` |
| `my_page` | `[my, page]` |
| `profile` | `[profile]` |

분리 기준: 하이픈(`-`), 언더스코어(`_`), 대문자 시작 경계(CamelCase). 모든 단어는 소문자로 정규화한다.

### 표기 변환표

단어 목록에서 아래 4가지 표기를 파생한다.

| 표기 | 변환 규칙 | 예시 (`[my, page]`) | 사용처 |
|---|---|---|---|
| `{name}` | 단어 이어 붙이기, 전체 소문자 | `mypage` | 모듈 디렉토리명 · 패키지명 · settings include · app dependency · route 값 |
| `{Name}` | PascalCase (각 단어 첫 글자 대문자) | `MyPage` | 클래스명 접두사 (Screen, ViewModel, Content 등) |
| `{NAME}` | UPPER_SNAKE_CASE (단어를 `_`로 연결) | `MY_PAGE` | route 상수명 (`MY_PAGE_ROUTE`) |
| `{camelName}` | camelCase (첫 단어 소문자, 이후 PascalCase) | `myPage` | Navigation 확장 함수명 |

단일 단어(예: `profile`)이면: `{name}`=`profile` / `{Name}`=`Profile` / `{NAME}`=`PROFILE` / `{camelName}`=`profile`

---

## 아키텍처 개요 (MVI / UDF)

생성되는 파일은 아래 단방향 데이터 흐름을 강제한다.

```
View ──Intent──▶ processIntent()
                     │
                  reduce()          ← 순수 함수, 동기 상태 전이
                     │
                  handleEffect()    ← 비동기 부수 효과 분기
                     │
              ┌──────┴──────┐
           uiState       sideEffect
              │               │
           View            View (LaunchedEffect)
```

- **Intent**: View → ViewModel 유일한 진입점
- **reduce**: 상태 전이 로직 (부수 효과 없음, 단위 테스트 용이)
- **handleEffect**: 비동기 작업 분기 (새 Intent 추가 시 여기와 private 함수만 수정)

---

## 실행 절차

### Step 1: 인자 검증

`$ARGUMENTS`가 비어 있으면 즉시 중단한다:
> 모듈명을 인자로 전달해주세요. 예: `/create-feature-module profile`

### Step 2: 중복 체크

```bash
ls feature/{name} 2>/dev/null && echo "EXISTS" || echo "OK"
```

`EXISTS`이면 중단한다:
> `feature/{name}` 모듈이 이미 존재합니다.

### Step 3: 디렉토리 생성

```bash
mkdir -p feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/component
mkdir -p feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model
mkdir -p feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/navigation
mkdir -p feature/{name}/src/test/kotlin/com/anddd/nevera
mkdir -p feature/{name}/src/androidTest/kotlin/com/anddd/nevera
```

### Step 4: 파일 생성

Write 툴로 아래 10개 파일을 순서대로 생성한다.

1. `feature/{name}/build.gradle.kts`
2. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}Intent.kt`
3. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}UiState.kt`
4. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}SideEffect.kt`
5. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/{Name}ViewModel.kt`
6. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/component/{Name}Content.kt`
7. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/{Name}Screen.kt`
8. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/navigation/{Name}Navigation.kt`
9. `feature/{name}/src/test/kotlin/com/anddd/nevera/ExampleUnitTest.kt`
10. `feature/{name}/src/androidTest/kotlin/com/anddd/nevera/ExampleInstrumentedTest.kt`

### Step 5: settings.gradle.kts 수정

`settings.gradle.kts` 파일에서 마지막 `include(":feature:...")` 라인 바로 다음에 삽입한다:

```kotlin
include(":feature:{name}")
```

### Step 6: app/build.gradle.kts 수정

`app/build.gradle.kts` 파일에서 마지막 `implementation(project(":feature:..."))` 라인 바로 다음에 삽입한다:

```kotlin
    implementation(project(":feature:{name}"))
```

### Step 7: 완료 보고

생성된 파일 목록을 출력하고, 아래 사항을 안내한다:
- `{Name}Intent` — feature 도메인에 맞는 Intent로 교체 (현재는 `Load` / `Submit` / `Reset` 예시)
- `{Name}UiState` — 도메인 필드 추가 (현재는 `status`만 포함)
- `{Name}Status` — 필요한 상태 추가 (현재는 `Idle` / `Loading` / `Success`)
- `{Name}SideEffect` — 도메인에 맞는 효과 추가 (현재는 `ShowToast` / `NavigateBack` 예시)
- `{Name}ViewModel` — `load()` / `submit()` 내 실제 UseCase 호출로 교체
- `{Name}Screen` — `onNavigateBack` 대신 feature에 맞는 navigation callback으로 교체
- `MainActivity` NavHost에 `{camelName}Screen()` 등록

---

## 파일 템플릿

### 1. build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anddd.nevera.feature.{name}"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":domain"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.timber)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
```

---

### 2. {Name}Intent.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

sealed interface {Name}Intent {
    data object Load : {Name}Intent
    data object Submit : {Name}Intent
    data object Reset : {Name}Intent
}
```

---

### 3. {Name}UiState.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

data class {Name}UiState(
    val status: {Name}Status = {Name}Status.Idle,
)

sealed interface {Name}Status {
    data object Idle : {Name}Status
    data object Loading : {Name}Status
    data object Success : {Name}Status
}
```

---

### 4. {Name}SideEffect.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

sealed interface {Name}SideEffect {
    data class ShowToast(val message: String) : {Name}SideEffect
    data object NavigateBack : {Name}SideEffect
}
```

---

### 5. {Name}ViewModel.kt

```kotlin
package com.anddd.nevera.feature.{name}.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.feature.{name}.main.model.{Name}Intent
import com.anddd.nevera.feature.{name}.main.model.{Name}SideEffect
import com.anddd.nevera.feature.{name}.main.model.{Name}Status
import com.anddd.nevera.feature.{name}.main.model.{Name}UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class {Name}ViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow({Name}UiState())
    val uiState: StateFlow<{Name}UiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<{Name}SideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    // ① 단일 진입점 — View → ViewModel 유일한 경로 (UDF 강제)
    fun processIntent(intent: {Name}Intent) {
        _uiState.update { reduce(it, intent) }
        handleEffect(intent)
    }

    // ② 순수 함수 — 동기적 상태 전이 (부수 효과 없음, 테스트 용이)
    private fun reduce(state: {Name}UiState, intent: {Name}Intent): {Name}UiState = when (intent) {
        {Name}Intent.Load -> state.copy(status = {Name}Status.Loading)
        {Name}Intent.Submit -> state.copy(status = {Name}Status.Loading)
        {Name}Intent.Reset -> {Name}UiState()
    }

    // ③ 비동기 부수 효과 분기 — 새 비동기 Intent 추가 시 여기와 private 함수만 수정
    private fun handleEffect(intent: {Name}Intent) {
        when (intent) {
            {Name}Intent.Load -> load()
            {Name}Intent.Submit -> submit()
            else -> Unit
        }
    }

    private fun load() {
        viewModelScope.launch {
            // TODO: UseCase로 초기 데이터 로드
            _uiState.update { it.copy(status = {Name}Status.Idle) }
        }
    }

    private fun submit() {
        viewModelScope.launch {
            // TODO: UseCase로 제출 처리
            _uiState.update { it.copy(status = {Name}Status.Success) }
            _sideEffect.send({Name}SideEffect.NavigateBack)
        }
    }
}
```

---

### 6. {Name}Content.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.{name}.main.model.{Name}Intent
import com.anddd.nevera.feature.{name}.main.model.{Name}UiState

@Composable
internal fun {Name}Content(
    uiState: {Name}UiState,
    onIntent: ({Name}Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        // TODO: UI 구현
        Text(text = "{Name}")
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun {Name}ContentPreview() {
    NeveraTheme {
        {Name}Content(
            uiState = {Name}UiState(),
            onIntent = {},
        )
    }
}
```

---

### 7. {Name}Screen.kt

```kotlin
package com.anddd.nevera.feature.{name}.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.{name}.main.component.{Name}Content
import com.anddd.nevera.feature.{name}.main.model.{Name}Intent
import com.anddd.nevera.feature.{name}.main.model.{Name}SideEffect
import com.anddd.nevera.feature.{name}.main.model.{Name}Status

@Composable
fun {Name}Screen(
    onNavigateBack: () -> Unit,
    viewModel: {Name}ViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.processIntent({Name}Intent.Load)
    }

    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is {Name}SideEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    {Name}SideEffect.NavigateBack -> onNavigateBack()
                }
            }
        }
    }

    NeveraTheme {
        when (uiState.status) {
            is {Name}Status.Loading -> LoadingContent()
            else -> {Name}Content(
                uiState = uiState,
                onIntent = viewModel::processIntent,
            )
        }
    }
}
```

---

### 8. {Name}Navigation.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.{name}.main.{Name}Screen

const val {NAME}_ROUTE = "{name}"

fun NavGraphBuilder.{camelName}Screen(
    onNavigateBack: () -> Unit,
) {
    composable(route = {NAME}_ROUTE) {
        {Name}Screen(onNavigateBack = onNavigateBack)
    }
}
```

---

### 9. ExampleUnitTest.kt

```kotlin
package com.anddd.nevera

import org.junit.jupiter.api.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assert(2 + 2 == 4)
    }
}
```

---

### 10. ExampleInstrumentedTest.kt

```kotlin
package com.anddd.nevera

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.anddd.nevera", appContext.packageName)
    }
}
```
