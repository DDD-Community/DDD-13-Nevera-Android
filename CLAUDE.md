# Nevera Android 개발 가이드

## AppBar 규칙

Scaffold의 `topBar` 파라미터에는 **반드시** 디자인 시스템에서 정의된 AppBar를 사용해야 합니다.
Material3 기본 AppBar(`TopAppBar`, `CenterAlignedTopAppBar` 등)를 직접 사용하지 않습니다.

### 사용 가능한 AppBar

| 컴포넌트 | 용도 |
|---|---|
| `NeveraAppBar` | 일반 상세·설정 화면. 중앙 제목 + 좌측 navigation + 우측 action |
| `NeveraDisplayAppBar` | 섹션 진입·타이틀 강조 화면. 좌측 큰 제목 + 우측 action |
| `NeveraLogoAppBar` | 메인·브랜드 화면. 좌측 로고 + 우측 action |
| `NeveraSearchAppBar` | 검색 화면. 검색 UI를 AppBar 내에 직접 배치 |

### 올바른 사용 예시

```kotlin
Scaffold(
    topBar = {
        NeveraAppBar(
            title = "화면 제목",
            navigation = NeveraAppBarNavigation.Back(onClick = onBackClick),
        )
    }
) { ... }
```

### 잘못된 사용 예시 (금지)

```kotlin
// Material3 기본 AppBar 직접 사용 금지
Scaffold(
    topBar = {
        TopAppBar(title = { Text("제목") })       // ❌
        CenterAlignedTopAppBar(title = { ... })   // ❌
    }
)
```
