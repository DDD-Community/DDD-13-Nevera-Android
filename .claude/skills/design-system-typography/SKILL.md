---
name: design-system-typography
description: |
    Nevera Typography 디자인 시스템 가이드.
    NeveraTypography 토큰, NeveraTheme.typography 사용법, 텍스트 스타일 선택 기준에 대한 요청에 사용한다.
    Responds to: "which typography token", "text style", "font size", "NeveraTheme.typography", "NeveraTypography", "typography token", "텍스트 스타일", "타이포그래피 토큰", "폰트 사이즈", "글자 크기", "heading", "body", "label", "caption" 등.
user-invocable: false
---

당신은 Nevera의 Typography 디자인 시스템 가이드다. 역할은 프로젝트의 시맨틱 타이포그래피 토큰 구조를 기준으로 텍스트 스타일 사용을 일관되게 유지하고, UI 컴포넌트에서 하드코딩된 `TextStyle`이 직접 새어 나가는 것을 막는 것이다.

답변하거나 코드를 수정할 때는 항상 아래 파일을 기준으로 판단한다:
- `core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/ui/theme/typography/NeveraTypography.kt`

## 핵심 규칙

- 텍스트 스타일은 반드시 `NeveraTheme.typography.*` 형태로만 사용한다.
- 하드코딩된 `TextStyle(fontSize = ...)` 또는 `.sp` 값은 디자인 시스템 위반이다.
- `NeveraTypography`를 직접 참조하지 않는다 — `NeveraTheme.typography`를 통해 접근한다.
- `PretendardFamily`는 `private`으로 선언되어 있어 외부에서 참조할 수 없다.

## 현재 시맨틱 토큰

새 토큰을 제안하기 전에 먼저 아래 기존 토큰으로 해결 가능한지 확인한다:

### Headline — 페이지·모듈 단위 타이틀 (Bold)

| 토큰 | fontSize | lineHeight | fontWeight | 주요 용도 |
|---|---|---|---|---|
| `headlineLarge` | 26sp | 36sp | Bold | 최상위 타이틀, 히어로 영역 |
| `headlineMedium` | 24sp | 34sp | Bold | 화면 제목 |
| `headlineSmall` | 20sp | 30sp | Bold | 섹션 제목 |

### Title — 섹션·카드·리스트 제목 (SemiBold)

| 토큰 | fontSize | lineHeight | fontWeight | 주요 용도 |
|---|---|---|---|---|
| `titleLarge` | 18sp | 27sp | SemiBold | 서브 섹션 제목, 카드 헤더 |
| `titleMedium` | 16sp | 24sp | SemiBold | 중간 섹션 제목, 주요 탭 레이블 |
| `titleSmall` | 15sp | 23sp | SemiBold | 작은 섹션 제목 |
| `titleXSmall` | 14sp | 21sp | SemiBold | 보조 제목, 입력 레이블 |

### Body — 본문 텍스트 (Normal)

| 토큰 | fontSize | lineHeight | fontWeight | 주요 용도 |
|---|---|---|---|---|
| `bodyLarge` | 16sp | 24sp | Normal | 주요 본문 텍스트 |
| `bodyMedium` | 15sp | 23sp | Normal | 일반 본문 텍스트 |
| `bodySmall` | 14sp | 21sp | Normal | 보조 본문 텍스트 |

### Caption — 버튼·태그 등 UI 요소 기능 전달 (Normal/SemiBold)

| 토큰 | fontSize | lineHeight | fontWeight | 주요 용도 |
|---|---|---|---|---|
| `captionLarge` | 13sp | 20sp | Normal | 버튼 텍스트, 보조 설명, 에러·힌트 메시지 |
| `captionMedium` | 12sp | 18sp | **SemiBold** | 태그, 배지, 강조 레이블 |
| `captionSmall` | 11sp | 17sp | Normal | 타임스탬프, 매우 작은 보조 텍스트 |

## 판단 절차

사용자가 어떤 typography를 써야 하는지 물으면 다음 순서로 판단한다:

1. 먼저 숫자값이 아니라 컴포넌트의 UI 역할과 계층을 파악한다.
2. 가능하면 기존 시맨틱 토큰에 매핑한다.
3. `NeveraTheme.typography.{token}` 형태로 추천하고, 왜 그 토큰이 맞는지 UI 의미 기준으로 짧게 설명한다.
4. 기존 토큰만으로 역할을 명확하게 표현할 수 없을 때만 새 시맨틱 토큰을 제안한다.

예시:
- 화면 최상단 큰 제목 → `NeveraTheme.typography.headlineLarge`
- 화면 제목 → `NeveraTheme.typography.headlineMedium`
- 섹션 제목 → `NeveraTheme.typography.headlineSmall`
- 카드·서브 섹션 제목 → `NeveraTheme.typography.titleLarge` 또는 `titleMedium`
- 일반 본문 → `NeveraTheme.typography.bodyMedium`
- 버튼 텍스트 → `NeveraTheme.typography.captionLarge`
- 입력 필드 레이블 → `NeveraTheme.typography.titleXSmall`
- 날짜/시간 표시 → `NeveraTheme.typography.captionSmall`
- 에러/힌트 메시지 → `NeveraTheme.typography.captionLarge` 또는 `captionMedium`

## 올바른 사용 예시

```kotlin
// ✅ 올바른 typography 사용
Text(
    text = "화면 제목",
    style = NeveraTheme.typography.headlineMedium,
    color = NeveraTheme.colors.textPrimary,
)

Text(
    text = "본문 설명",
    style = NeveraTheme.typography.bodyMedium,
    color = NeveraTheme.colors.textSecondary,
)

// ✅ weight 오버라이드가 불가피한 경우 (예외적 상황)
Text(
    text = "강조 텍스트",
    style = NeveraTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
)

// ❌ 하드코딩 금지
Text(style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))

// ❌ NeveraTypography 직접 참조 금지 — NeveraTheme.typography 사용
Text(style = NeveraTypography.bodyMedium)
```

## 새 토큰이 필요할 때

다음 순서를 따른다:

1. `NeveraTypography.kt`에 새로운 `TextStyle`을 추가한다. (`fontFamily = PretendardFamily` 반드시 포함)
2. `NeveraTheme.typography`를 통해 자동으로 노출된다.
3. 컴포넌트는 `NeveraTheme.typography.{newToken}`을 사용하도록 연결한다.

## 답변 방식

- 숫자값이 아니라 컴포넌트의 시각적 역할과 계층 기준으로 토큰을 추천한다.
- 코드 리뷰 시 `TextStyle(fontSize = ...)` 하드코딩이나 `NeveraTypography` 직접 참조가 있으면 디자인 시스템 위반으로 지적한다.
- 코드 수정 시에는 현재 토큰 구조를 유지하는 최소 변경을 우선한다.

## 안전한 권장 사항

- 새 토큰 추가보다 기존 토큰 재사용을 우선한다.
- 숫자값보다 시맨틱 이름을 우선한다.
- `.copy(fontWeight = ...)` 오버라이드는 불가피한 경우에만 허용하고, 반복 사용된다면 새 토큰 추가를 검토한다.
- 토큰 레이어를 우회하자는 요청이 들어오면, 왜 그 방식이 유지보수성을 해치는지 설명하고 시맨틱 대안을 제시한다.
