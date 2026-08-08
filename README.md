<div align="center">
  <img src="docs/image/nevera_presentation.png" alt="식구(Sikgu) — 버려지는 식재료는 0원, 통장 잔고는 +α!" width="680"/>

# 식구(Sikgu)

**버려지는 식재료는 0원, 통장 잔고는 +α!**

냉장고 속 식재료를 금액으로 관리하는 앱입니다.<br/>
영수증을 찍어 식재료를 등록하고, 버리지 않고 다 먹은 만큼을 금액으로 환산해 갖고 싶던 것에 쌓아 갑니다.

<sub><code>Nevera</code>는 저장소·코드베이스 코드명입니다. · DDD 13기에서 Android 2인으로 진행한 팀 프로젝트입니다.</sub>

</div>

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **영수증 OCR 등록** | 영수증을 촬영하면 서버가 품목을 추출하고, 진행률을 실시간으로 받아 보여준 뒤 식재료를 일괄 등록합니다. |
| **냉장고 관리** | 냉장·냉동·실온 보관 위치와 9개 카테고리로 분류하고, 유통기한순·최신순으로 정렬합니다. |
| **구조 / 폐기 집계** | 식재료를 `구조`(다 먹음) 또는 `폐기`로 처리하면 비율에 따라 금액이 집계됩니다. 부분 처리도 누적됩니다. |
| **위시 달성** | 구조한 금액이 사용자가 정한 위시(갖고 싶은 것)의 목표 금액에 누적됩니다. |
| **유통기한 알림** | 사용자가 정한 시각에 서버가 FCM 푸시를 보내고, 앱은 수신한 알림을 WorkManager로 알림함(Room)에 영속화합니다. |

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| **Language** | Kotlin 2.3 |
| **UI** | Jetpack Compose, Material3, Navigation Compose |
| **Architecture** | Clean Architecture (Multi-Module), MVI (Orbit) |
| **DI** | Hilt |
| **Async** | Coroutines, Flow |
| **Network** | Retrofit, OkHttp (SSE) |
| **Local** | Room, DataStore |
| **Image** | Coil 3 |
| **Auth** | Google Credential Manager |
| **Firebase** | FCM, Crashlytics |
| **Background** | WorkManager |
| **Quality** | Detekt (커스텀 룰), JUnit5, MockK |
| **Logging** | Timber |

---

## 아키텍처

### Clean Architecture (Multi-Module)

의존성 방향은 항상 바깥 레이어 → 안쪽 레이어(Domain)로만 향합니다. `domain`은 `nevera.kotlin.jvm`을 적용한 순수 Kotlin 모듈이라 Android 프레임워크도 Robolectric도 없이 JVM에서 바로 돌고, 그 덕에 UseCase·도메인 규칙 테스트 64개가 에뮬레이터 없이 CI에서 실행됩니다.

| 레이어 | 역할 |
|--------|------|
| **Feature** | 화면(Composable)과 ViewModel로 구성되는 독립적인 기능 단위. 다른 feature에 의존하지 않는다. |
| **Domain** | UseCase · Repository Interface · Domain Model을 담은 순수 Kotlin 모듈. Android 의존성 없음. |
| **Data** | Repository 구현체와 Remote(Retrofit) · Local(Room · DataStore) DataSource를 제공한다. |
| **Core** | 여러 feature가 공유하는 DesignSystem · Network · Database · MVI 기반 등의 범용 라이브러리. |
| **Infra** | FCM · WorkManager를 활용한 푸시 알림 처리 등 플랫폼 인프라 레이어. |

#### 응답이 값이 아니라 흐름인 경우 — OCR 진행률

영수증 OCR은 서버 처리가 수 초 걸려 단발 요청/응답으로는 진행률을 보여줄 수 없습니다. OkHttp의 SSE(`EventSources`)로 진행률 이벤트를 구독하고 `callbackFlow`로 감싸, Domain에는 `Flow`만 노출했습니다. ([`OcrProgressDataSourceImpl`](data/src/main/kotlin/com/anddd/nevera/data/datasource/OcrProgressDataSourceImpl.kt))

타임아웃은 세 단계로 나뉩니다. 일반 API용 공용 클라이언트는 `readTimeout` 30초, OCR 분석은 서버 처리가 길어 300초짜리 전용 클라이언트(`@OcrExtractOkHttpClient`)를 따로 둡니다. 그런데 SSE는 스트림이 닫힐 때까지 연결을 유지해야 해서 300초로도 부족합니다. 이 전용 클라이언트의 타임아웃을 0으로 바꾸면 같은 클라이언트를 쓰는 OCR 업로드 요청까지 무제한이 되므로, `newBuilder()`로 파생해 `readTimeout`만 해제한 SSE 전용 인스턴스를 한 겹 더 만들었습니다.

### 모듈 구조

```text
Nevera
├── app                     애플리케이션 진입점 · 네비게이션 그래프 조립
├── build-logic             Convention Plugin 11종 (includeBuild — 별도 빌드)
├── domain                  UseCase · Repository Interface · Domain Model (순수 Kotlin)
├── data                    Repository 구현 · Remote(Retrofit · SSE) · Local(Room · DataStore)
├── core
│   ├── common              공용 유틸리티 (순수 Kotlin)
│   ├── designsystem        컬러 · 타이포그래피 · 스페이싱 토큰과 Nevera 컴포넌트
│   ├── ui                  feature 공용 Compose 확장
│   ├── network             Retrofit · OkHttp · 인터셉터 구성
│   ├── database            Room · DataStore 구성
│   └── mvi                 NeveraViewModel — Intent → Mutation → State 계약
├── infra
│   ├── notification        FCM 수신 · 알림 영속화 · 토큰 동기화 Worker
│   └── permission          런타임 권한 처리
├── feature
│   ├── splash              스플래시 · 최초 진입 분기
│   ├── auth                로그인 · 회원가입
│   ├── main                홈 (위시 진행률 · 구조/폐기 요약)
│   ├── fridge              냉장고 식재료 목록 · 편집
│   ├── ingredient          영수증 OCR 촬영 · 식재료 등록 · 구조/폐기 처리
│   ├── notification        알림함
│   ├── mypage              마이페이지 · 계정 · 알림 설정
│   └── sample              디자인 시스템 샘플 화면
└── quality
    └── detekt-rules        프로젝트 아키텍처 규칙을 검사하는 Detekt 커스텀 룰
```

`build-logic`의 Convention Plugin이 `compileSdk`·Compose·Hilt·테스트 의존성 등 반복 설정을 흡수합니다. 예를 들어 `feature:fridge`의 `build.gradle.kts`는 전부 이렇습니다. Compose·Hilt·직렬화 플러그인, `core:*`·`domain` 의존성, ViewModel 테스트 도구는 모두 `nevera.feature` 안에 들어 있습니다.

```kotlin
plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.fridge"
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(project(":infra:permission"))
}
```

플러그인별 구성과 조합 기준은 [build-logic README](build-logic/README.md)에 정리되어 있습니다.

### MVI 상태 관리

Orbit이 제공하는 `reduce` · `postSideEffect`가 `Syntax` 스코프 안에서만 호출될 수 있다는 컴파일 타임 보장을 활용하기 위해 [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi)를 채택했습니다.

그 위에 `Intent → Mutation → State` 흐름을 강제하는 [`NeveraViewModel`](core/mvi/src/main/kotlin/com/anddd/nevera/core/mvi/NeveraViewModel.kt)을 정의해, 모든 feature ViewModel이 `handleIntent` · `applyMutation` 두 메서드만 구현하면 되도록 보일러플레이트를 제거했습니다.

```text
Screen
  └─ Intent ──→ handleIntent (비즈니스 로직)
                    ├─ postSideEffect ──────────────────→ SideEffect → Screen
                    └─ Mutation ──→ applyMutation
                                        └─ reduce ─────→ State → Screen
```

다만 Orbit이 보장해 주는 것은 `reduce`가 `Syntax` 스코프 안에서만 호출된다는 것까지입니다. `intent { }` 안에서 `applyMutation`을 건너뛰고 `reduce`를 직접 부르는 것은 컴파일러가 막지 못합니다. **타입 시스템이 닿지 않는 이 마지막 한 칸을 Detekt 커스텀 룰로 메웠습니다.**

자세한 설계 원칙과 도입 근거는 [MVI 도입 및 core:mvi 설계 원칙](docs/mvi-core-architecture.md)을 참고해 주세요.

---

## 아키텍처 규칙의 강제

컨벤션을 문서로만 두면 지켜지는지 확인할 방법이 없습니다. Nevera는 프로젝트 아키텍처 규칙을 **Detekt 커스텀 룰 9종**으로 구현해 [`quality:detekt-rules`](quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality) 모듈에 두고, 위반 시 빌드를 실패시킵니다.

룰셋은 `nevera.feature` Convention Plugin이 내부에서 적용하므로 검사 대상은 feature 모듈 8개입니다. 규칙 자체가 `*Screen` · `*Content` · `*ViewModel` 계약을 다루고, 그 계약이 성립하는 곳이 feature 레이어뿐이라 범위를 여기에 맞췄습니다.

| 룰 | 강제하는 규칙 |
|----|--------------|
| `NeveraViewModelInheritanceRule` | feature 모듈의 ViewModel은 반드시 `NeveraViewModel`을 상속한다 |
| [`ReduceOutsideApplyMutationRule`](quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/ReduceOutsideApplyMutationRule.kt) | `reduce { }`는 `applyMutation()` 내부에서만 호출한다 |
| `SealedInterfaceContractRule` | `*Intent` · `*Mutation` · `*SideEffect`는 반드시 `sealed interface`로 선언한다 |
| `ContentComposableParameterRule` | `*Content`의 파라미터는 `*UiState` · 함수 타입 · `Modifier` · `LazyPagingItems`만 허용한다 |
| `ScreenDelegatesToContentRule` | `*Screen`은 같은 접두사의 `*Content`를 호출해 렌더링을 위임한다 |
| `ScreenNoScaffoldRule` | `*Screen.kt` **파일 어디에서도** `Scaffold`를 호출하지 않는다. private 하위 컴포저블로 숨기는 우회까지 막는다 |
| `ViewModelAccessOnlyInScreenRule` | ViewModel 주입·상태 구독(`hiltViewModel`, `collectAsState` 등)은 `*Screen`과 `@Preview` 함수에서만 한다 |
| `ToastOutsideScreenRule` | `Toast.makeText`는 이름이 `Screen`으로 끝나는 Composable 안에서만 호출한다 |
| `Material3AppBarRule` | Material3 기본 AppBar 5종(`TopAppBar` · `CenterAlignedTopAppBar` · `Small`/`Medium`/`LargeTopAppBar`)을 어디서도 호출하지 않는다. `NeveraAppBar` 계열을 쓴다 |

```kotlin
// ❌ ReduceOutsideApplyMutationRule 위반 — detekt 실패
private fun onRefreshClicked() = intent {
    reduce { state.copy(isLoading = true) }
}

// ✅ 상태 변경의 의미를 Mutation으로 남기고, reduce는 applyMutation 한 곳에서만
private fun onRefreshClicked() = intent {
    applyMutation(HomeMutation.Loading)
}
```

각 룰은 대응하는 테스트 파일과 1:1로 짝을 이룹니다.

위 표는 룰이 **실제로 강제하는 범위**를 적은 것이고, 프로젝트 컨벤션은 그보다 좁은 경우가 있습니다. 예를 들어 `Toast`는 이름이 `Screen`으로 끝나는 Composable 안이기만 하면 detekt를 통과하지만, 컨벤션은 SideEffect 처리부에서 띄우기를 요구합니다. 이렇게 룰이 닿지 않는 간격은 코드 리뷰가 담당합니다. 컨벤션 전문은 [CLAUDE.md](CLAUDE.md)에 있습니다.

### 스타일이 아니라 아키텍처만 검사한다

[`config/detekt/detekt.yml`](config/detekt/detekt.yml)은 Detekt **빌트인 룰셋 9개를 전부 비활성화**하고(`style`, `naming`, `complexity`, `potential-bugs` 등), 위 커스텀 룰셋 3종만 켠 뒤 `warningsAsErrors: true`로 운영합니다.

들여쓰기나 네이밍 지적으로 CI가 빨개지면 팀은 곧 경고를 무시하게 됩니다. 그래서 **막을 가치가 있는 아키텍처 위반만 빌드를 세우도록** 범위를 좁혔습니다. 코드 스타일은 IDE 포매터와 리뷰에 맡깁니다.

룰셋 범위를 이렇게 정한 배경은 [Screen/Content 경계 룰 설계 기록](docs/execplan-detekt-screen-content-boundary.md)과 [MVI 품질 게이트 설계 기록](docs/execplan-mvi-quality-gates.md)에 남아 있습니다.

---

## 테스트

테스트 파일 54개, `@Test` 401개로 구성되어 있습니다. (Android Studio가 모듈 생성 시 만드는 템플릿 스텁은 제외한 수치입니다.)

| 대상 | 도구 | 파일 | 테스트 | CI |
|------|------|------|--------|----|
| **`data`** — Repository 구현 · 응답 매핑 | JUnit5, MockK, coroutines-test | 14 | 148 | ✅ |
| **`domain`** — UseCase · 도메인 모델 | JUnit5, MockK | 8 | 64 | ✅ |
| **`feature`** — ViewModel (auth · main · fridge · mypage) | JUnit5, MockK, orbit-test | 4 | 64 | ✅ |
| **`quality:detekt-rules`** — 커스텀 룰 | Detekt test 유틸 | 9 | 47 | ✅ |
| **`infra`** — 알림 · 권한 (단위) | JUnit5, MockK | 3 | 26 | ✅ |
| **`core:designsystem` · `infra:permission`** — 계측 테스트 | Compose ui-test-junit4 | 16 | 52 | 로컬 |

테스트는 도메인 규칙과 응답 매핑에 집중시켰습니다. `data`(148개)의 대부분은 서버 응답 → 도메인 모델 변환과 에러 코드 매핑이고, feature 레이어는 ViewModel의 Intent → State 전이만 `orbit-test`로 검증합니다. ViewModel이 의존하는 UseCase는 인터페이스가 아니라 클래스라 손으로 대역을 만들 수 없어 MockK를 쓰고, 그 아래 Repository 계약은 `domain` 테스트가 이미 검증하므로 중복해서 내려가지 않습니다.

테스트 의존성은 `NeveraTestUnitPlugin` · `NeveraTestAndroidPlugin` Convention Plugin으로 묶어, 모듈마다 개별 선언하지 않습니다.

**CI가 게이트하는 건 JVM 단위 테스트 349개입니다.** 계측 테스트 52개는 에뮬레이터가 필요해 CI에서 제외했습니다. 에뮬레이터 부팅 비용을 매 PR에 물리는 대신 디자인 시스템 변경 시 수동 실행으로 두었고, CI 편입은 실행 시간을 측정한 뒤 결정할 문제로 남겨 두었습니다.

이 중 `core:designsystem`의 50개는 에뮬레이터 준비·부팅 대기·정리까지 처리하는 스크립트로 실행합니다.

```bash
scripts/android/run-designsystem-compose-tests.sh
```

이 스크립트는 `:core:designsystem:connectedDebugAndroidTest`만 실행합니다. `infra:permission`의 계측 테스트 2개는 범위 밖이라 따로 돌려야 합니다.

```bash
./gradlew :infra:permission:connectedDebugAndroidTest
```

설계 배경은 [테스트 기반 구축 기록](docs/execplan-test-foundation.md)과 [디자인 시스템 Compose 테스트 기록](docs/execplan-designsystem-compose-tests.md)에 정리되어 있습니다.

---

## CI/CD

| 워크플로 | 트리거 | 동작 |
|----------|--------|------|
| [`ci.yml`](.github/workflows/ci.yml) | `develop` PR · push | Unit Test · Lint · Detekt |
| [`cd-firebase.yml`](.github/workflows/cd-firebase.yml) | `develop` · `release/**` push, 수동 | Firebase App Distribution 배포 |
| [`pr-auto-assign.yml`](.github/workflows/pr-auto-assign.yml) | PR `opened` · `reopened` | assignee · reviewer 자동 등록 |
| [`release-drafter.yml`](.github/workflows/release-drafter.yml) | `develop` push | 릴리스 노트 초안 자동 작성 |

**품질 게이트는 마지막에 한 번에 판정합니다.** 세 검사(Unit Test · Lint · Detekt)를 각각 `continue-on-error`로 끝까지 실행하고 리포트를 모두 아티팩트로 올린 뒤, 마지막 step에서 세 결과를 합산해 실패시킵니다. 첫 검사에서 중단해버리면 나머지 리포트를 받지 못해 수정 사이클이 한 번 더 늘어나기 때문입니다.

**배포 대상은 브랜치로 갈립니다.** `develop` 머지는 `android-developers` 그룹에, `release/**` 브랜치는 `android-developers, qa-and-team` 그룹에 배포됩니다. 릴리스 노트는 Release Drafter가 만들어 둔 초안 본문을 브랜치명·빌드 번호 뒤에 이어 붙입니다. 긴급 배포를 위한 수동 트리거(`workflow_dispatch`)도 열어 두되, 기본 브랜치에서 잘못 실행되지 않도록 브랜치 검증 step으로 막았습니다. fork 저장소에서는 배포 job 자체가 스킵됩니다.

---

## 개발 환경

| 항목 | 버전 |
|------|------|
| Android Studio | Meerkat 이상 권장 |
| AGP | 9.1.0 |
| Kotlin | 2.3.0 |
| JDK | 17 |
| Min SDK | 30 |
| Target SDK | 36 |
| Compile SDK | 36 |

---

## AI Agent(Claude)

이 저장소에는 Claude Code 전용 스킬과 훅이 포함되어 있어, 반복 작업을 자동화하고 프로젝트 규칙을 코드 작성 흐름 안에서 강제합니다.

### 스킬

| 카테고리 | 스킬 |
|----------|------|
| **[개발 자동화](docs/aiagent/skills/dev-automation.md)** (4종) | `/create-feature-module` · `/implement-compose-preview` · `/recommend-commit-message` · `/sync-develop` |
| **[PR / CI](docs/aiagent/skills/pr-ci.md)** (3종) | `/create-pr` · `/pr-auto-assign` · `/ci-android` |
| **[디자인 시스템](docs/aiagent/skills/design-system.md)** (4종) | `/design-system-color` · `/design-system-typography` · `/design-system-spacing` · `/design-system-shape` |
| **[테스트](docs/aiagent/skills/testing.md)** (1종) | `/run-designsystem-compose-ui-test` |

각 스킬의 동작 방식과 트리거 조건은 [📖 AI Agent 가이드](docs/aiagent/README.md)에 정리되어 있습니다.

### 훅

| 훅 | 트리거 | 동작 |
|----|--------|------|
| [`check-appbar`](docs/aiagent/hooks/check-appbar.md) | `.kt` 편집 직후 (`PostToolUse`) | `Scaffold`의 `topBar`에 Nevera AppBar를 썼는지 검사하고 위반 시 경고 |
| [`check-detekt`](docs/aiagent/hooks/check-detekt.md) | 응답 종료 시 (`Stop`) | 변경된 `.kt`가 있으면 `detekt`를 실행하고, 실패하면 종료를 막아 계속 고치게 함 |

`check-detekt`는 에이전트가 스스로 수정하도록 종료를 차단하되, **연속 3회마다 한 번은 반드시 통과시켜** 제어권이 사람에게 돌아오게 합니다. 카운터는 detekt 실행 **전에** 기록해 훅 타임아웃(300초)으로 프로세스가 강제 종료돼도 횟수가 보존되게 했고, detekt 출력은 임시 파일에 쓰고 Python에는 **출력 내용이 아니라 파일 경로와 재시도 횟수만** 인자로 넘겨, 셸 보간을 거치지 않게 해 커맨드 인젝션을 차단합니다.
