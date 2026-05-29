---
name: create-feature-module
description: |
  feature 모듈 껍데기를 MVI 구조로 생성한다. "feature 모듈 만들어줘", "feature 껍데기 생성", "create-feature-module" 등의 요청에 응답한다.
  build.gradle.kts, Intent/Screen/ViewModel/UiState/SideEffect/Mutation/Navigation/Content 파일 생성,
  settings.gradle.kts 및 app/build.gradle.kts 자동 등록까지 한 번에 처리한다.
  $ARGUMENTS로 모듈명(소문자, 예: profile / user-profile)을 받는다.
argument-hint: <모듈명 (예: profile, user-profile)>
version: 0.3.0
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
| `{name}` | 단어 이어 붙이기, 전체 소문자 | `mypage` | 모듈 디렉토리명 · 패키지명 · settings include · app dependency |
| `{Name}` | PascalCase (각 단어 첫 글자 대문자) | `MyPage` | 클래스명 접두사 (Screen, ViewModel, Content 등) |
| `{NAME}` | UPPER_SNAKE_CASE (단어를 `_`로 연결) | `MY_PAGE` | (현재 기본 템플릿에서 미사용, 필요 시 커스텀 상수에 활용) |
| `{camelName}` | camelCase (첫 단어 소문자, 이후 PascalCase) | `myPage` | Navigation 확장 함수명 |

단일 단어(예: `profile`)이면: `{name}`=`profile` / `{Name}`=`Profile` / `{NAME}`=`PROFILE` / `{camelName}`=`profile`

---

## 아키텍처 개요 (Orbit MVI / UDF)

`core:mvi` 모듈의 `NeveraViewModel`을 기반으로 하는 Orbit MVI 패턴을 사용한다.

```
[View]      viewModel.handleIntent(Intent)
                │
                ▼
[ViewModel] handleIntent(intent)    ← NeveraViewModel abstract override, 단일 진입점
                │  when(intent) → onXxx()
                ▼
            intent {}               ← Orbit coroutine scope
                │
                ▼
            applyMutation(Mutation) ← suspend, Syntax<STATE, SIDE_EFFECT> context
             ┌────┴────┐
           reduce    postSideEffect
             │            │
           STATE      SIDE_EFFECT
             │            │
[View]  collectAsState  collectSideEffect
```

- **NeveraIntent**: View → ViewModel 유일한 진입점 (marker interface)
- **NeveraMutation**: 상태 전이 기술 단위 — `applyMutation`에서 `reduce`로 적용
- **NeveraState**: UI 렌더링용 불변 상태 (marker interface)
- **NeveraSideEffect**: 토스트·네비게이션 등 단발성 부수 효과 (marker interface)

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

Write 툴로 아래 12개 파일을 순서대로 생성한다.

1. `feature/{name}/build.gradle.kts`
2. `feature/{name}/src/main/AndroidManifest.xml`
3. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}Intent.kt`
4. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}UiState.kt`
5. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}SideEffect.kt`
6. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/model/{Name}Mutation.kt`
7. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/{Name}ViewModel.kt`
8. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/component/{Name}Content.kt`
9. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/{Name}Screen.kt`
10. `feature/{name}/src/main/kotlin/com/anddd/nevera/feature/{name}/main/navigation/{Name}Navigation.kt`
11. `feature/{name}/src/test/kotlin/com/anddd/nevera/ExampleUnitTest.kt`
12. `feature/{name}/src/androidTest/kotlin/com/anddd/nevera/ExampleInstrumentedTest.kt`

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
- `{Name}Intent` — feature 도메인에 맞는 Intent로 교체 (현재는 `Load` / `Submit` 예시)
- `{Name}UiState` — 도메인 필드 추가 (현재는 `isLoading: Boolean`만 포함)
- `{Name}SideEffect` — 도메인에 맞는 효과 추가 (현재는 `ShowToast` / `NavigateBack` 예시)
- `{Name}Mutation` — 도메인에 맞는 상태 전이 케이스 추가 (현재는 `LoadSuccess` 예시)
- `{Name}ViewModel` — `load()` / `submit()` 내 실제 UseCase 호출로 교체
- `{Name}Screen` — `onNavigateBack` 대신 feature에 맞는 navigation callback으로 교체
- `MainActivity` NavHost에 `{camelName}Screen()` 등록

---

## 파일 템플릿

### 1. build.gradle.kts

```kotlin
plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.{name}"
}

dependencies {
    implementation(libs.coroutines.android)
}
```

---

### 2. AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

---

### 3. {Name}Intent.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface {Name}Intent : NeveraIntent {
    data object Load : {Name}Intent
    data object Submit : {Name}Intent
}
```

---

### 4. {Name}UiState.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class {Name}UiState(
    val isLoading: Boolean = false,
) : NeveraState
```

---

### 5. {Name}SideEffect.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface {Name}SideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : {Name}SideEffect
    data object NavigateBack : {Name}SideEffect
}
```

---

### 6. {Name}Mutation.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface {Name}Mutation : NeveraMutation {
    data object LoadSuccess : {Name}Mutation
}
```

---

### 7. {Name}ViewModel.kt

```kotlin
package com.anddd.nevera.feature.{name}.main

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.{name}.main.model.{Name}Intent
import com.anddd.nevera.feature.{name}.main.model.{Name}Mutation
import com.anddd.nevera.feature.{name}.main.model.{Name}SideEffect
import com.anddd.nevera.feature.{name}.main.model.{Name}UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class {Name}ViewModel @Inject constructor() :
    NeveraViewModel<{Name}UiState, {Name}SideEffect, {Name}Intent, {Name}Mutation>({Name}UiState()) {

    init {
        handleIntent({Name}Intent.Load)
    }

    override fun handleIntent(intent: {Name}Intent) {
        when (intent) {
            {Name}Intent.Load -> load()
            {Name}Intent.Submit -> submit()
        }
    }

    private fun load() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: UseCase로 초기 데이터 로드
        applyMutation({Name}Mutation.LoadSuccess)
    }

    private fun submit() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: UseCase로 제출 처리
        postSideEffect({Name}SideEffect.NavigateBack)
    }

    override suspend fun Syntax<{Name}UiState, {Name}SideEffect>.applyMutation(
        mutation: {Name}Mutation,
    ) {
        when (mutation) {
            {Name}Mutation.LoadSuccess -> reduce { state.copy(isLoading = false) }
        }
    }
}
```

---

### 8. {Name}Content.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.{name}.main.model.{Name}Intent
import com.anddd.nevera.feature.{name}.main.model.{Name}UiState

@Composable
internal fun {Name}Content(
    uiState: {Name}UiState,
    onIntent: ({Name}Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TODO: UI 구현
            Text(text = "{Name}")
        }
        if (uiState.isLoading) {
            LoadingContent()
        }
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

### 9. {Name}Screen.kt

```kotlin
package com.anddd.nevera.feature.{name}.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.{name}.main.component.{Name}Content
import com.anddd.nevera.feature.{name}.main.model.{Name}SideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun {Name}Screen(
    onNavigateBack: () -> Unit,
    viewModel: {Name}ViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is {Name}SideEffect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            {Name}SideEffect.NavigateBack -> onNavigateBack()
        }
    }

    {Name}Content(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
```

---

### 10. {Name}Navigation.kt

```kotlin
package com.anddd.nevera.feature.{name}.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anddd.nevera.feature.{name}.main.{Name}Screen
import kotlinx.serialization.Serializable

@Serializable
data object {Name}Route

fun NavGraphBuilder.{camelName}Screen(
    onNavigateBack: () -> Unit,
) {
    composable<{Name}Route> {
        {Name}Screen(onNavigateBack = onNavigateBack)
    }
}
```

---

### 11. ExampleUnitTest.kt

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

### 12. ExampleInstrumentedTest.kt

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
