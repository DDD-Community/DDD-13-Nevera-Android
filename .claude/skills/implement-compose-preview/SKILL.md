---
name: implement-compose-preview
description: |
  This skill should be used when the user asks to "Preview 만들어줘", "Preview 추가해줘",
  "composable preview 생성", "@Preview 함수 만들어줘", "compose preview 붙여줘", or wants
  to generate @Preview functions for a Composable file or function.
  Also trigger autonomously (without user request) whenever a new top-level @Composable
  function is written or completed in a .kt file — generate @Preview functions immediately
  after writing the composable, before moving on.
  Takes a file path or composable function name as $ARGUMENTS.
argument-hint: <파일경로 또는 Composable함수명>
version: 0.2.0
---

# implement-compose-preview

## 역할

`$ARGUMENTS`로 전달받은 파일 경로 또는 Composable 함수명을 분석하여,
프로젝트 컨벤션에 맞는 `@Preview` 함수를 생성하고 해당 파일 하단에 추가한다.

## 인자 파싱

`$ARGUMENTS`를 다음 기준으로 해석한다.

| 인자 유형 | 판별 기준 | 처리 방법 |
|-----------|-----------|-----------|
| 파일 경로 | `.kt` 확장자 포함 또는 경로 구분자(`/` 또는 `\`) 포함 | 파일 내 모든 `@Composable` public 함수를 대상으로 Preview 생성 |
| 함수명 | `.kt`, `/` 미포함 | 해당 이름의 `@Composable` 함수를 찾아 해당 함수만 Preview 생성 |
| 없음 | `$ARGUMENTS`가 비어있음 | **자율 실행 컨텍스트**: 직전에 작성하거나 편집한 `.kt` 파일을 자동으로 대상으로 삼는다. 파일을 특정할 수 없을 때만 사용자에게 질문한다. |

## 실행 절차

### Step 1: 대상 파일 및 함수 파악

`$ARGUMENTS`를 파싱하여 대상 파일 경로와 Composable 함수를 식별한다.

- 파일 경로인 경우: 해당 파일을 읽어 `@Composable` 어노테이션이 붙은 **top-level public 함수(수신 객체 없이 직접 호출 가능한 함수)** 목록을 파악한다.
- 함수명인 경우: 프로젝트 내에서 해당 함수를 검색하여 파일 위치와 시그니처를 파악한다. 동일 이름 함수가 여러 개면(오버로드/다른 파일) 후보를 나열하고 사용자에게 대상 함수를 선택받는다.

### Step 2: Composable 함수 시그니처 분석

각 대상 Composable 함수의 파라미터를 다음 기준으로 분석한다.

- **enum 타입**: 모든 entry에 대해 각각 Preview를 별도 생성한다. 이 규칙은 최상위 파라미터뿐 아니라 sealed 하위 타입의 생성자 파라미터 안에 중첩된 enum에도 동일하게 적용한다. 예를 들어 `AppBarAction.Text(text: String, tone: Tone)` 처럼 nested enum 이 있으면 `Tone.Primary`, `Tone.Tertiary` 각각에 대해 별도 Preview를 생성한다.
- **sealed class 타입**: 모든 하위 타입에 대해 각각 Preview를 별도 생성하되, 하위 타입 형태에 따라 인스턴스화 방식을 다르게 적용한다.
  - `object` 하위 타입: 해당 object를 그대로 사용한다.
  - `class` / `data class` 하위 타입: 파라미터를 이 분석 규칙에 따라 재귀적으로 처리하여 인스턴스를 생성한다 (String → 한국어 샘플, Boolean → `true` 고정, List → 1~3개 더미 아이템 등).
  - 생성자에 `vararg` 파라미터가 있으면, 해당 원소 타입 기준으로 더미 값을 필요한 개수만큼 채워 넣어 클래스 불변식을 만족시킨다. 예를 들어 `AppBarAction.Icons(vararg items: Item)` 는 구체 클래스명 `AppBarAction.Icons` 와 원소 타입 `Item` 을 기준으로 처리하고, `AppBarAction.Icons.init` 의 `require(items.size in 1..2)` 를 만족하도록 `Item` 더미를 1개 또는 2개 생성해야 한다.
  - `vararg Item` 더미 생성 시에도 기존 샘플 규칙을 그대로 적용한다 (String → 한국어 샘플, Boolean → `true`, List → 1~3개 더미 엔트리 등). 즉 preview generator 는 `AppBarAction.Icons` 를 만들 때 `Item` 인스턴스를 1~2개 구성해 전달해야 한다.
- **Boolean 파라미터** (`enabled`, `selected`, `isLoading` 등): 각 Boolean 파라미터를 **독립적으로** 처리한다. 파라미터마다 `true` / `false` 두 Preview를 생성하되, 해당 파라미터 외의 Boolean은 기본값(기본값 없으면 `false`)으로 고정한다.
  - 단, enum으로 이미 표현되는 상태와 중복되면 생략한다. 중복 판단은 이름 기반 휴리스틱을 우선 사용한다. Boolean 이름이 `is`, `has`, `should` 로 시작하거나 `enabled`, `active`, `selected`, `loading`, `expanded` 같은 상태 키워드를 포함하고, enum 타입명 또는 enum entry 가 같은 상태 키워드(`Enabled` / `Disabled`, `Selected` / `Unselected`, `Loading` / `Idle` 등)를 직접 표현하면 중복으로 본다.
  - enum entry 와 Boolean 의미의 직접 매핑이 불명확하면 자동으로 생략하지 말고 Boolean Preview도 함께 생성한다. 함수명이 모호하거나 enum 설명만으로 상태 대응을 확신할 수 없으면 사용자에게 어떤 Boolean 을 생략할지 확인받는다.
- **lambda 파라미터** (`onClick`, `onDismiss` 등): `{}` 빈 람다로 채운다.
- **String 파라미터**: 의미 있는 한국어 샘플 텍스트를 사용한다 (예: `"타이틀"`, `"닉네임"`, `"확인"`).
- **List / Map 파라미터**: 기본적으로 1~3개의 더미 아이템을 포함하는 샘플 데이터를 구성한다. 단, 함수명에 `Empty` / `Placeholder`가 포함되거나 파라미터명이 빈 상태를 명시하는 경우(예: `emptyItems`, `placeholderData`)에는 `emptyList()` / `emptyMap()`을 사용한다.
- **Modifier 파라미터**: 기본값이 있으면 생략한다.

### Step 3: Preview 함수 작성

분석 결과를 바탕으로 아래 패턴에 따라 `@Preview` 함수를 작성한다.

**단일 상태 컴포넌트** (variant 없음):

```kotlin
@Preview(
    name = "[ComponentName]",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun [ComponentName]Preview() {
    NeveraTheme {
        [ComponentName](
            // 파라미터
        )
    }
}
```

**다중 상태 컴포넌트** (enum 또는 Boolean variant):

```kotlin
@Preview(
    name = "[ComponentName] - [Variant1]",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun [ComponentName][Variant1]Preview() {
    NeveraTheme {
        [ComponentName](
            type = [EnumType].[Variant1],
        )
    }
}

@Preview(
    name = "[ComponentName] - [Variant2]",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun [ComponentName][Variant2]Preview() {
    NeveraTheme {
        [ComponentName](
            type = [EnumType].[Variant2],
        )
    }
}
```

### Step 4: 파일 하단에 추가

생성한 Preview 함수들을 **대상 Composable 함수가 위치한 파일의 가장 하단**에 추가한다.

- 기존 코드와 빈 줄 하나로 구분한다.
- 동일한 이름의 Preview 함수가 이미 존재하면 덮어쓰지 않고 사용자에게 알린다.

## 컨벤션 요약

| 항목 | 규칙 |
|------|------|
| 테마 | 항상 `NeveraTheme {}` 로 감싸기 |
| 배경 | `showBackground = true` |
| 너비 | `widthDp = 360` |
| 접근제어 | `private` |
| 네이밍 | `[ComponentName][Variant]Preview` |
| 샘플 텍스트 | 한국어 (예: `"타이틀"`, `"닉네임"`, `"확인"`, `"취소"`) |
| 다중 variant | enum / sealed 타입마다 별도 Preview 함수 |
| 위치 | 파일 가장 하단 |
