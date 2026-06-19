<div align="center">
  <img src="docs/image/nevera_presentation.png" alt="Nevera App Banner" width="100%"/>
</div>

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| **Language** | Kotlin 2.3 |
| **UI** | Jetpack Compose, Material3, Navigation Compose |
| **Architecture** | Clean Architecture (Multi-Module), MVI (Orbit) |
| **DI** | Hilt |
| **Async** | Coroutines, Flow |
| **Network** | Retrofit, OkHttp |
| **Local** | Room, DataStore |
| **Image** | Coil 3 |
| **Auth** | Google Credential Manager |
| **Firebase** | FCM, Crashlytics |
| **Background** | WorkManager |
| **Logging** | Timber |

---

## 아키텍처

### Clean Architecture (Multi-Module)

의존성 방향은 항상 바깥 레이어 → 안쪽 레이어(Domain)로만 향합니다.

| 레이어 | 역할 |
|--------|------|
| **Feature** | 화면(Composable)과 ViewModel로 구성되는 독립적인 기능 단위. 다른 feature에 의존하지 않는다. |
| **Domain** | UseCase · Repository Interface · Domain Model을 담은 순수 Kotlin 모듈. Android 의존성 없음. |
| **Data** | Repository 구현체와 Remote(Retrofit) · Local(Room · DataStore) DataSource를 제공한다. |
| **Core** | 여러 feature가 공유하는 DesignSystem · Network · Database · MVI 기반 등의 범용 라이브러리. |
| **Infra** | FCM · WorkManager를 활용한 푸시 알림 처리 등 플랫폼 인프라 레이어. |

### MVI 상태 관리

Orbit이 제공하는 `reduce` · `intent` · `postSideEffect`가 각각의 스코프 안에서만 호출될 수 있다는 컴파일 타임 보장을 활용하기 위해 [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi)를 채택했습니다.

그 위에 `Intent → Mutation → State` 흐름을 강제하는 `NeveraViewModel`을 정의해, 모든 feature ViewModel이 `handleIntent` · `applyMutation` 두 메서드만 구현하면 되도록 보일러플레이트를 제거했습니다. `applyMutation`은 Orbit `Syntax` 스코프 안에서만 실행되므로 상태 변경과 부수 효과가 의도된 경로 밖에서 발생하는 것을 컴파일 단계에서 차단합니다.

```
Screen
  └─ Intent ──→ handleIntent (비즈니스 로직)
                    ├─ postSideEffect ──────────────────→ SideEffect → Screen
                    └─ Mutation ──→ applyMutation
                                        └─ reduce ─────→ State → Screen
```

#### MVI Convention

Nevera의 MVI 구조는 단순히 Orbit MVI를 사용하는 것에 그치지 않고, 프로젝트 차원의 규칙을 추가로 정의합니다.

- 사용자 액션은 `Intent`로 표현합니다.
- 화면 상태는 `UiState`로 표현합니다.
- 일회성 이벤트는 `SideEffect`로 표현합니다.
- 상태 변경의 의미는 `Mutation`으로 표현합니다.
- 실제 `reduce` 호출은 `applyMutation()` 한 곳에 모읍니다.

이 규칙을 통해 화면에서 발생 가능한 액션과 상태 변경 흐름을 코드만으로 추적하기 쉽게 만듭니다.

자세한 설계 원칙과 도입 근거는 [MVI 도입 및 core:mvi 설계 원칙](docs/mvi-core-architecture.md)을 참고해 주세요.

---

## 개발 환경

| 항목 | 버전 |
|------|------|
| Android Studio | Meerkat 이상 권장 |
| AGP | 9.1.0 |
| Kotlin | 2.3.0 |
| Min SDK | 26 |
| Target SDK | 35 |
| Compile SDK | 35 |

---

## AI Agent(Claude)
이 프로젝트에는 Claude Code 전용 스킬과 훅이 내장되어 있어, 반복 작업을 자동화하고 프로젝트 규칙을 자동으로 강제합니다.
자세한 내용은 **[📖 AI Agent 가이드](docs/aiagent/README.md)** 를 참고해 주세요.

### Claude SKILLs

| 카테고리 | 스킬 | 설명 | 유형 |
|----------|------|------|------|
| **개발 자동화** | [`/create-feature-module`](docs/aiagent/skills/dev-automation.md#create-feature-module) | Feature 모듈 MVI 보일러플레이트 자동 생성 | 🛠️ 실행 |
| | [`/implement-compose-preview`](docs/aiagent/skills/dev-automation.md#implement-compose-preview) | Composable에 `@Preview` 함수 자동 생성 | 🛠️ 실행 |
| | [`/recommend-commit-message`](docs/aiagent/skills/dev-automation.md#recommend-commit-message) | Staged 변경사항 분석 후 커밋 메시지 추천 | 🛠️ 실행 |
| | [`/sync-develop`](docs/aiagent/skills/dev-automation.md#sync-develop) | upstream develop 브랜치 동기화 & push | 🛠️ 실행 |
| **PR / CI** | [`/create-pr`](docs/aiagent/skills/pr-ci.md#create-pr) | 브랜치 분석 → 코드 리뷰 → PR 템플릿 작성 → PR 생성 | 🛠️ 실행 |
| | [`/pr-auto-assign`](docs/aiagent/skills/pr-ci.md#pr-auto-assign) | PR Auto Assign 워크플로우 가이드 | 📖 참조 |
| | [`/ci-android`](docs/aiagent/skills/pr-ci.md#ci-android) | GitHub Actions CI 구조 및 설정 가이드 | 📖 참조 |
| **디자인 시스템** | [`/design-system-color`](docs/aiagent/skills/design-system.md#design-system-color) | 시맨틱 컬러 토큰 선택 가이드 | 📖 참조 |
| | [`/design-system-typography`](docs/aiagent/skills/design-system.md#design-system-typography) | 타이포그래피 토큰 선택 가이드 | 📖 참조 |
| | [`/design-system-spacing`](docs/aiagent/skills/design-system.md#design-system-spacing) | 스페이싱 토큰 및 패딩/갭 가이드 | 📖 참조 |
| | [`/design-system-shape`](docs/aiagent/skills/design-system.md#design-system-shape) | Corner radius 토큰 선택 가이드 | 📖 참조 |

> 🛠️ **실행** — `/스킬명` 직접 호출 또는 자연어로 트리거하면 코드 생성·액션을 즉시 실행합니다.  
> 📖 **참조** — `/스킬명` 직접 호출 불가. 관련 질문·요청 시 Claude Code가 자동으로 참조해 답변에 반영합니다.

### Claude Hooks

| 파일 | 트리거 | 검증 내용 |
|------|--------|----------|
| [`check-appbar.sh`](docs/aiagent/hooks/check-appbar.md) | `.kt` 파일 편집 직후 | `Scaffold`의 `topBar`에 `TopAppBar` 등 Material3 기본 AppBar 대신 Nevera AppBar 사용 여부 |
