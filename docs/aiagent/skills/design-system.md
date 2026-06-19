# 디자인 시스템 스킬

Nevera 디자인 시스템의 컬러·타이포그래피·스페이싱·Shape 토큰 사용을 안내하는 스킬 모음입니다.

> 이 카테고리의 스킬은 모두 `user-invocable: false`로 설정되어 있습니다. `/스킬명` 직접 호출 대신, 관련 질문이나 코드 작성 요청 시 Claude Code가 자동으로 참조해 정확한 토큰을 안내합니다.

## 목차

- [/design-system-color](#design-system-color) — 시맨틱 컬러 토큰 가이드
- [/design-system-typography](#design-system-typography) — 타이포그래피 토큰 가이드
- [/design-system-spacing](#design-system-spacing) — 스페이싱 토큰 가이드
- [/design-system-shape](#design-system-shape) — Shape(Corner radius) 토큰 가이드

---

### design-system-color

**시맨틱 컬러 토큰 선택을 안내하고, 컬러 시스템 수정을 도와줍니다.**

**트리거 예시**
```
배경색 어떤 토큰 써야 해?
버튼 텍스트 색상은?
다크모드 컬러 어떻게 추가해?
새 컬러 토큰 추가하고 싶어
```

**참조 파일**
- `core/designsystem/.../color/ColorPalette.kt` — 원시 팔레트 (내부용)
- `core/designsystem/.../color/NeveraColor.kt` — 시맨틱 토큰 레이어 (UI에서 사용)

**2-tier 구조 규칙**
- UI 코드에서는 반드시 `NeveraTheme.colors.{token}` 형태로만 사용
- `ColorPalette`를 컴포넌트에서 직접 참조하지 않는다

**주요 토큰 카테고리**: Text · Icon · Primary(오렌지) · Secondary(그레이) · Accent · Background · Surface · Border · Divider · Status(Information/Positive/Negative/Warning) · Notification

---

### design-system-typography

**타이포그래피 토큰 선택을 안내합니다.**

**트리거 예시**
```
화면 제목 폰트 뭐 써야 해?
body 텍스트 스타일은?
14sp짜리 텍스트 어떤 토큰이야?
```

**참조 파일**
- `core/designsystem/.../typography/NeveraTypography.kt`

**핵심 규칙**: `NeveraTheme.typography.*` 로만 접근, `TextStyle(fontSize = X.sp)` 하드코딩 금지

**토큰 체계**

| 그룹 | 특징 | 토큰 예시 |
|------|------|-----------|
| Headline | Bold, 페이지/섹션 타이틀 | `headlineLarge(26sp)` · `headlineMedium(24sp)` · `headlineSmall(20sp)` |
| Title | SemiBold, 카드/리스트 제목 | `titleLarge(18sp)` · `titleMedium(16sp)` · `titleSmall(15sp)` |
| Body | Regular, 본문 | `bodyLarge(16sp)` · `bodyMedium(15sp)` · `bodySmall(14sp)` |
| Label | Medium, 버튼/태그 레이블 | `labelLarge(15sp)` · `labelMedium(14sp)` · `labelSmall(12sp)` |
| Caption | Regular, 보조 설명 | `captionMedium(13sp)` · `captionSmall(12sp)` |

---

### design-system-spacing

**스페이싱 토큰 선택과 `padding` / `gap` / `margin` 사용을 안내합니다.**

**트리거 예시**
```
카드 내부 패딩 얼마로 줘야 해?
아이템 간격 어떤 토큰 써야 해?
화면 좌우 여백 표준이 뭐야?
```

**참조 파일**
- `core/designsystem/.../spacing/NeveraSpacing.kt`

**핵심 규칙**: `NeveraTheme.spacing.*` 로만 접근, 하드코딩된 `.dp` 사용 금지 (단, 애니메이션 offset 등 디자인 토큰이 없는 특수 목적은 예외)

**토큰 체계** (4의 배수 기반 8pt 그리드)

| 토큰 | 값 | 주요 용도 |
|------|----|-----------|
| `spacing2` | 2dp | 아이콘-텍스트 최소 간격 |
| `spacing4` | 4dp | 인라인 요소 내부 간격 |
| `spacing8` | 8dp | 컴포넌트 내부 소간격 |
| `spacing12` | 12dp | 중간 간격 |
| `spacing16` | 16dp | 화면 기본 수평 패딩, 섹션 간격 |
| `spacing20` | 20dp | 큰 섹션 간격 |
| `spacing24` | 24dp | 카드/섹션 내부 패딩 |
| `spacing32` | 32dp | 큰 블록 간격 |
| `spacing40` | 40dp | 페이지 레벨 상하 여백 |
| `spacing48` | 48dp | 히어로 영역 여백 |

---

### design-system-shape

**Corner radius(모서리 둥글기) 토큰 선택을 안내합니다.**

**트리거 예시**
```
버튼 모서리 얼마나 둥글게 해야 해?
카드 corner radius는?
pill 모양 버튼 만드려면?
NeveraRadius 뭐 써야 해?
```

**참조 파일**
- `core/designsystem/.../shape/NeveraRadius.kt`

**핵심 규칙**: `NeveraTheme.radius.*` 로만 접근, 하드코딩된 `.dp` RoundedCornerShape 사용 금지

**토큰 체계**

| 토큰 | 값 | 주요 용도 |
|------|----|-----------|
| `radius4` | 4dp | 작은 칩, 태그 |
| `radius8` | 8dp | 입력 필드, 작은 카드 |
| `radius12` | 12dp | 중간 카드, 리스트 아이템 |
| `radius16` | 16dp | 큰 카드, 바텀시트 상단 |
| `radius24` | 24dp | 큰 모달, 강조 섹션 |
| `radiusFull` | 999dp | Pill 버튼, FAB, 아바타 |
