# 컬러 시스템 설계 철학 및 사용 가이드

## 컬러 계층 구조 (2-tier)

```
ColorPalette (internal)         NeveraColor (public)
─────────────────────           ──────────────────────────────
원시 색상값 저장소               시맨틱 토큰 (역할 기반)
외부 접근 불가 (internal)        컴포넌트에서 유일하게 참조 가능
모든 색상의 단일 원본            Light/Dark 인스턴스로 분리
```

- **`ColorPalette`** : 브랜드 팔레트 원본. `primary`, `gray`, `onPrimary` 스케일 보관.
  디자인팀 컬러가 바뀌면 이 파일만 수정.
- **`NeveraColor`** : 컴포넌트가 참조하는 시맨틱 토큰.
  `background*`, `text*`, `brand*`, `disabled*`, `border*`, `divider` 카테고리로 구성.

## 시맨틱 토큰 사용 규칙

컴포넌트에서 `ColorPalette`를 직접 참조하는 것을 **금지**한다.
반드시 `NeveraTheme.colors.{semanticToken}` 형태로만 참조한다.

```kotlin
// ❌ 금지
val color = NeveraTheme.colors.primary400

// ✅ 올바른 사용
val color = NeveraTheme.colors.brandPrimary
```

## 토큰 추가/변경 가이드

### 새 색상이 필요할 때

1. `ColorPalette.kt` — 원시 색상값 추가
2. `NeveraColor.kt` — 시맨틱 필드 추가 (클래스 + `LightNeveraColors` + `DarkNeveraColors`)
3. 컴포넌트에서 새 시맨틱 토큰 참조

### 기존 색상값을 바꿀 때 (디자인팀 업데이트)

`ColorPalette.kt`만 수정 → 시맨틱 매핑은 그대로 유지됨

### 다크모드 컬러 적용 시

`DarkNeveraColors`의 팔레트 매핑값만 변경.
`NeveraColor` 클래스 구조 변경 불필요.

## 현재 시맨틱 토큰 목록

| 카테고리 | 토큰 | Light 값 |
|---|---|---|
| Background | `backgroundPrimary` | white |
| Background | `backgroundSecondary` | gray50 |
| Text | `textPrimary` | gray800 |
| Text | `textSecondary` | gray600 |
| Text | `textTertiary` | gray500 |
| Text | `textDisabled` | gray400 |
| Text | `textInverse` | white |
| Brand | `brandPrimary` | primary400 |
| Brand | `onBrandPrimary` | onPrimary400 |
| Brand | `brandPrimarySubtle` | primary50 |
| Brand | `onBrandPrimarySubtle` | onPrimary500 |
| Disabled | `disabledContainer` | gray300 |
| Disabled | `disabledContent` | gray500 |
| Border | `borderDefault` | gray300 |
| Divider | `divider` | gray200 |
