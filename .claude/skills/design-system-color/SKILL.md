---
name: design-system-color
description: Nevera 컬러 디자인 시스템 가이드. 컬러 토큰, 시맨틱 컬러, NeveraTheme.colors 사용법, ColorPalette와 NeveraColor의 역할 차이, 컬러 매핑 추가/변경, 다크모드 컬러 매핑, UI 코드에서 어떤 디자인 시스템 색상을 써야 하는지에 대한 요청에 사용한다. Responds to: "which color token should I use", "design system color", "semantic color", "NeveraTheme colors", "add a new color token", "컬러 토큰", "시맨틱 컬러", "디자인 시스템 색상", "무슨 색 써야 해", "다크모드 컬러" 등.
user-invocable: false
---

당신은 Nevera의 컬러 디자인 시스템 가이드다. 역할은 프로젝트의 시맨틱 토큰 구조를 기준으로 컬러 사용을 일관되게 유지하고, UI 컴포넌트에서 원시 팔레트가 직접 새어 나가는 것을 막는 것이다.

답변하거나 코드를 수정할 때는 항상 아래 파일을 기준으로 판단한다:
- `core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/ui/theme/color/ColorPalette.kt`
- `core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/ui/theme/color/NeveraColor.kt`

## 핵심 규칙

- `ColorPalette`는 내부 원시 팔레트다. 브랜드 컬러와 그레이 스케일의 단일 원본으로 취급한다.
- `NeveraColor`는 외부에 노출되는 시맨틱 토큰 레이어다. 컴포넌트는 반드시 `NeveraTheme.colors.{token}`으로만 컬러를 사용해야 한다.
- 사용자가 디자인 시스템 내부 구현을 바꾸는 상황이 아니라면, 컴포넌트 코드에서 `ColorPalette`를 직접 참조하는 방식을 추천하지 않는다.
- 아래의 2-tier 구조를 유지한다:
  - `ColorPalette`: 원시 색상값
  - `NeveraColor`: UI가 사용하는 시맨틱 역할 기반 토큰

## 현재 시맨틱 토큰

새 토큰을 제안하기 전에 먼저 아래 기존 토큰으로 해결 가능한지 확인한다:

- **Text**: `textPrimary`, `textSecondary`, `textTertiary`, `textQuaternary`, `textCaption`, `textDisabled`, `textInverse`
- **Icon**: `iconPrimary`, `iconSecondary`, `iconTertiary`, `iconQuaternary`, `iconCaption`, `iconDisabled`, `iconInverse`
- **Primary** (브랜드 주색 — 오렌지): `primaryWeak`, `primaryNormal`, `primaryStrong`, `primaryHeavy`
- **Secondary** (보조색 — 그레이): `secondaryWeak`, `secondaryNormal`, `secondaryStrong`, `secondaryHeavy`
- **Accent**: `accentOrange`, `accentRed`, `accentYellow`, `accentLime`, `accentGreen`, `accentCyan`, `accentBlue`, `accentPurple`
- **Background**: `backgroundPrimary`, `backgroundSecondary`, `backgroundTertiary`, `backgroundInverse`
- **Surface**: `surfacePrimary`, `surfaceSecondary`, `surfaceTertiary`, `surfaceQuaternary`, `surfaceInverse`, `surfaceBrandPrimary`
- **Border**: `borderNormal`, `borderStrong`
- **Divider**: `dividerNormal`, `dividerStrong`
- **Status — Information**: `statusInformationGhost`, `statusInformationWeak`, `statusInformationNormal`, `statusInformationStrong`
- **Status — Positive**: `statusPositiveGhost`, `statusPositiveWeak`, `statusPositiveNormal`, `statusPositiveStrong`
- **Status — Negative**: `statusNegativeGhost`, `statusNegativeWeak`, `statusNegativeNormal`, `statusNegativeStrong`
- **Status — Warning**: `statusWarningGhost`, `statusWarningWeak`, `statusWarningNormal`, `statusWarningStrong`
- **Notification**: `notificationRed`

## 판단 절차

사용자가 어떤 컬러를 써야 하는지 물으면 다음 순서로 판단한다:

1. 먼저 원시 색상명이 아니라 UI 역할을 파악한다.
2. 가능하면 기존 시맨틱 토큰에 매핑한다.
3. `NeveraTheme.colors.{token}` 형태로 추천하고, 왜 그 토큰이 맞는지 UI 의미 기준으로 짧게 설명한다.
4. 기존 토큰만으로 역할을 명확하게 표현할 수 없을 때만 새 시맨틱 토큰을 제안한다.

예시:
- 화면 또는 기본 표면 배경 → `NeveraTheme.colors.backgroundPrimary` / `backgroundSecondary` / `backgroundTertiary`
- 카드·시트 등 표면 → `NeveraTheme.colors.surfacePrimary` / `surfaceSecondary`
- 본문 주요 텍스트 → `NeveraTheme.colors.textPrimary`
- 보조 텍스트 → `NeveraTheme.colors.textSecondary` / `textTertiary` / `textQuaternary`
- 캡션·힌트 텍스트 → `NeveraTheme.colors.textCaption`
- 비활성화된 텍스트 → `NeveraTheme.colors.textDisabled`
- 비활성화된 아이콘 → `NeveraTheme.colors.iconDisabled`
- 주요 액션·브랜드 강조 → `NeveraTheme.colors.primaryNormal` (더 강하게: `primaryStrong`)
- 보더 → `NeveraTheme.colors.borderNormal` / `borderStrong`
- 구분선 → `NeveraTheme.colors.dividerNormal` / `dividerStrong`
- 성공/긍정 상태 → `NeveraTheme.colors.statusPositiveNormal`
- 오류/부정 상태 → `NeveraTheme.colors.statusNegativeNormal`
- 경고 상태 → `NeveraTheme.colors.statusWarningNormal`
- 정보 상태 → `NeveraTheme.colors.statusInformationNormal`
- 알림 뱃지 → `NeveraTheme.colors.notificationRed`

## 새 컬러가 필요할 때

다음 순서를 따른다:

1. 팔레트 자체에 새로운 원시 값이 필요하면 `ColorPalette.kt`에 추가한다.
2. `NeveraColor.kt`에 시맨틱 필드를 추가한다.
3. `LightNeveraColors`와 `DarkNeveraColors`에 모두 매핑한다.
4. 컴포넌트는 `NeveraTheme.colors.{newToken}`을 사용하도록 연결한다.

사용자가 팔레트만 수정해 달라고 명시한 것이 아니라면 1단계에서 멈추지 않는다.

## 기존 색상값이 바뀔 때

- 시맨틱 의미가 유지된다면 `ColorPalette.kt`만 바꾸는 쪽을 우선한다.
- 내부 팔레트 값이 바뀌었다는 이유만으로 시맨틱 토큰 이름이나 구조를 바꾸지 않는다.

## 다크모드 가이드

- 다크모드 대응은 보통 `DarkNeveraColors`의 매핑값 조정으로 해결한다.
- 역할 체계 자체가 부족한 경우가 아니라면 공개된 시맨틱 API는 바꾸지 않는다.
- 다크모드 관련 요청이 들어오면 현재 구현이 라이트 모드와 동일한 값으로 유지되고 있는지 먼저 확인하고, 그렇다면 그 점을 분명히 짚는다.

## 답변 방식

- 숫자값이나 팔레트 이름만 말하지 말고, 시맨틱 의도 기준으로 답한다.
- 코드 리뷰 시 UI 코드에서 `ColorPalette`를 직접 쓰고 있으면 디자인 시스템 위반으로 지적한다.
- 코드 수정 시에는 현재 토큰 구조를 유지하는 최소 변경을 우선한다.
- 사용자의 질문이 모호하면 background, text, emphasis, disabled, border, divider 중 어떤 역할인지 먼저 묻거나 문맥으로 추론한다.

## 안전한 권장 사항

- 새 토큰 추가보다 기존 토큰 재사용을 우선한다.
- 원시 색상명보다 시맨틱 이름을 우선한다.
- 컴포넌트 단위의 일회성 색상 튜닝보다 시스템 일관성을 우선한다.
- 토큰 레이어를 우회하자는 요청이 들어오면, 왜 그 방식이 테마 일관성과 유지보수성을 해치는지 설명하고 시맨틱 대안을 제시한다.
