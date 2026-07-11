# Screen/Content 경계 강제 Detekt 규칙 구축 및 기존 위반 화면 정리

이 ExecPlan은 살아있는 문서다. `Progress`, `Surprises & Discoveries`, `Decision Log`, `Outcomes & Retrospective` 섹션은 작업이 진행되는 동안 반드시 최신 상태로 유지해야 한다.

이 문서는 저장소 루트의 `PLANS.md`에 정의된 규칙에 따라 작성되고 유지되어야 한다.


## Purpose / Big Picture


Nevera Android 프로젝트의 feature 모듈은 화면을 세 계층으로 나눈다. `*Screen`은 ViewModel을 구독하고 일회성 이벤트(SideEffect)를 처리하는 진입점이고, `*Content`는 화면 상태(`UiState`)를 받아 레이아웃을 그리는 순수 렌더러이며, 그 아래 Component들이 세부 UI를 조립한다. 이 규칙은 `docs/mvi-presentation-layer-structure.md`, `docs/screen-content-srp-responsibility.md`, `docs/compose-screen-content-separation.md`에 문서로 정리되어 있지만, 현재 코드 작성 시 자동으로 검증되지 않는다. 실제로 이 문서 규칙을 위반하는 화면이 코드베이스에 이미 존재한다(아래 `Surprises & Discoveries`의 스파이크 결과 참조).

이 ExecPlan을 완료하면 다음이 가능해진다. 첫째, `./gradlew detekt`를 실행하면 Screen/Content 경계 위반(Screen이 Content 없이 직접 렌더링, Screen 파일의 Scaffold 사용, Content에서의 ViewModel 접근, Screen 밖 Toast 호출)이 즉시 검출된다. 둘째, 기존 위반 화면 4곳이 규칙에 맞게 수정되어 전체 detekt가 통과한다. 셋째, 이 검사는 별도 설정 없이 기존 CI(`.github/workflows/ci.yml`의 detekt 스텝)와 Claude Code Stop 훅(`.claude/hooks/check-detekt.sh`)에 자동으로 편입된다 — 두 경로 모두 이미 `./gradlew detekt`를 실행하고 있기 때문이다.

기존 MVI 규칙(`NeveraMviRules` 룰셋의 4개 규칙)과 책임이 겹치지 않도록, 이번 규칙들은 별도 룰셋 `NeveraScreenContentRules`로 분리한다. 기존 규칙은 "MVI 타입 계약"(ViewModel 상속, reduce 호출 위치, sealed interface 선언, Content 파라미터 타입)을 다루고, 신규 규칙은 "Compose 계층 간 경계"(누가 무엇을 호출할 수 있는가)를 다룬다.


## Progress


- [ ] 마일스톤 1: 신규 detekt 규칙 4개 구현 및 단위 테스트 — `ScreenDelegatesToContentRule`, `ScreenNoScaffoldRule`, `ViewModelAccessOnlyInScreenRule`, `ToastOutsideScreenRule` + `ContentComposableParameterRule`에 `LazyPagingItems` 허용 추가
- [ ] 마일스톤 2: 전체 코드베이스 위반 수집 — `./gradlew detekt --continue`로 전 모듈 위반 목록 확정 (스파이크에서 6건 확인됨, 재확인 목적)
- [ ] 마일스톤 3: 위반 화면 단계적 수정 — SignupScreen(Toast 헬퍼), PhotoDetailScreen·RegisterSuccessScreen(Content 분리), IngredientScreen(Scaffold 이동), NotificationScreen(NotificationList → NotificationContent 개명·이동)
- [ ] 마일스톤 4: 최종 검증 및 문서 상호 참조 — 전체 detekt 통과, 단위 테스트 통과, 문서 3종에 규칙 ID 역참조 추가


## Surprises & Discoveries


이 계획 수립 전, 동일 브랜치에서 규칙 4개를 프로토타입으로 구현해 전체 코드베이스에 실행하는 스파이크를 수행했고, 결과 확인 후 전부 롤백했다(작업 트리는 현재 깨끗함). 스파이크에서 확인된 사실은 다음과 같으며, 본 계획은 이 사실들을 전제로 한다.

- 관찰: `./gradlew detekt`는 기본 fail-fast라서 첫 위반 모듈(`:feature:auth:detekt`)에서 빌드가 중단되고 이후 모듈은 검사되지 않는다. 처음 실행에서 위반이 1건만 보였던 이유였다.
  증거: `--continue` 플래그를 붙이자 6건 전체가 출력됨. `feature/ingredient/build/reports/detekt/detekt.txt`의 수정 시각이 이틀 전 그대로였던 것으로 미실행을 확인.

- 관찰: 스파이크 기준 전체 위반은 6건이다. (1) `feature/auth/.../signup/SignupScreen.kt:60` — Screen 함수 밖 `showToast` 헬퍼의 Toast 호출, (2) `feature/ingredient/.../main/IngredientScreen.kt:90` — Screen 파일의 Scaffold 직접 호출, (3) `feature/ingredient/.../photodetail/PhotoDetailScreen.kt:45` — PhotoDetailContent 미호출, (4) `feature/ingredient/.../registersuccess/RegisterSuccessScreen.kt:35` — RegisterSuccessContent 미호출, (5) `feature/notification/.../main/NotificationScreen.kt:60` — NotificationContent 미호출, (6) 같은 파일 `:120` — private `NotificationList` 안의 Scaffold.
  증거: 스파이크 실행 시 detekt 콘솔 출력에서 규칙 ID(`ScreenDelegatesToContentRule`, `ScreenNoScaffoldRule`, `ToastOutsideScreenRule`)와 함께 위 위치가 보고됨.

- 관찰: `NotificationScreen.kt`의 `NotificationList`는 `pagingItems: LazyPagingItems<NotificationItemUiModel>` 파라미터를 받는다. 이를 `NotificationContent`로 개명하면 기존 `ContentComposableParameterRule`(Content 파라미터는 UiState/함수 타입/Modifier만 허용)에 걸린다. Paging 라이브러리는 Flow를 Screen에서 `collectAsLazyPagingItems()`로 수집해 값으로 내려보내는 것이 공식 패턴이므로, `LazyPagingItems`를 허용 타입에 추가해야 두 규칙이 충돌하지 않는다.
  증거: `feature/notification/.../NotificationScreen.kt:110` 시그니처 확인.

- 관찰: detekt 1.23.8의 테스트 유틸리티 `io.github.detekt.test.utils.compileContentForTest(content, filename)`과 `Rule.lint(ktFile)` 조합으로 파일명 기반 규칙(`ScreenNoScaffoldRule`)을 단위 테스트할 수 있다. 스파이크에서 이 조합으로 작성한 테스트 전체가 첫 컴파일에 통과했다.
  증거: 스파이크 실행 시 `./gradlew :quality:detekt-rules:test` BUILD SUCCESSFUL.


## Decision Log


- 결정: 신규 규칙 4개를 기존 `NeveraMviRules`가 아닌 새 룰셋 `NeveraScreenContentRules`로 분리한다.
  근거: 기존 룰셋은 MVI 타입 계약(상속, sealed, reduce 위치, 파라미터 타입)을 다루고, 신규 규칙은 Compose 계층 경계(호출 가능 위치)를 다룬다. 관심사가 다르므로 `RuleSetProvider`를 분리하면 config(`detekt.yml`)에서도 두 영역이 별도 최상위 키로 구분되어 유지보수가 쉽다. 기존 규칙과의 중복 방지라는 사용자 요구사항과도 일치한다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: `ScreenDelegatesToContentRule`은 ViewModel이 없는 Screen에도 예외 없이 적용한다. 이에 따라 ViewModel 없는 화면(PhotoDetail, RegisterSuccess)에는 ViewModel 없이 Screen이 직접 조립하는 경량 `*UiState` data class를 도입해 Content 파라미터 계약(`*UiState` + 콜백 + Modifier)을 그대로 지킨다.
  근거: 화면마다 "분리할지 말지"를 판단하게 두면 예외가 늘어나며, "왜 이 화면만 다른가"를 추론하는 비용이 분리 비용을 넘어선다. 프로젝트 전반의 통일성과 SRP 유지가 우선이라는 사용자 결정.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: `ScreenNoScaffoldRule`을 포함하고, 함수 단위가 아닌 파일 단위(`*Screen.kt`)로 검사한다.
  근거: `IngredientScreen`의 Scaffold는 정당한 예외가 아니라 아직 분리되지 않은 부채다 — phase별 분기는 결국 `uiState.phase`로 결정되는 UiState-driven 렌더링이므로 Content 책임이다. 파일 단위로 검사하는 이유는 Screen 파일 안의 private 하위 컴포저블(현재 `NotificationList`가 실제 사례)로 Scaffold를 숨기는 우회를 막기 위해서다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: `ViewModelAccessOnlyInScreenRule`은 "Content에서 금지" 방식이 아니라 "Screen에서만 허용" 방식으로 뒤집어 설계한다. 금지 대상 호출은 `hiltViewModel`, `viewModel`, `collectAsState`, `collectAsStateWithLifecycle`, `collectSideEffect`, `collectAsLazyPagingItems`이고, `*Screen` 이름의 함수 내부(중첩 포함)와 `@Preview` 함수 내부만 허용한다.
  근거: Content는 파일명·함수명으로 특정할 수 있지만 Component(WishBanner, IngredientItem 등)는 이름 규칙이 없어 "Content에서 금지" 방식으로는 커버할 수 없다. 허용 지점(Screen)이 유일하므로 역방향 규칙이 커버리지가 넓고 구현도 단순하다. `@Preview` 예외는 `NotificationScreen.kt`의 Preview가 `flowOf(...).collectAsLazyPagingItems()`로 가짜 페이징 데이터를 만드는 정당한 실사용 사례가 있기 때문이다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: `ToastOutsideScreenRule`은 feature 패키지 전체에서 `Toast.makeText`를 검사하되 `*Screen` 함수 내부만 허용한다. ViewModel 안의 Toast도 위반으로 처리한다.
  근거: Content/Component의 Toast는 recomposition마다 반복 실행되는 런타임 버그를 만들고(`docs/screen-content-srp-responsibility.md` 6장), ViewModel의 Toast는 플랫폼 의존을 만든다. 올바른 경로는 ViewModel이 SideEffect를 발행하고 Screen의 `collectSideEffect`에서 Toast를 띄우는 것 하나뿐이다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: 기존 `ContentComposableParameterRule`의 허용 파라미터 타입에 `LazyPagingItems`를 추가한다.
  근거: Paging 3 라이브러리의 공식 사용 패턴은 Screen에서 `collectAsLazyPagingItems()`로 수집한 값을 하위 컴포저블에 전달하는 것이다. 이 타입을 허용하지 않으면 `NotificationList`를 `NotificationContent`로 개명하는 순간 기존 규칙과 신규 규칙이 서로 충돌해 통과 불가능한 코드가 된다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: 모든 신규 규칙에 "패키지 이름에 `feature` 포함" 가드를 둔다.
  근거: 기존 `NeveraViewModelInheritanceRule`과 동일한 방식으로, core/디자인시스템/샘플 코드의 오탐을 방지한다. Screen/Content 계층 규칙은 feature 모듈의 Presentation Layer에만 적용되는 규칙이다.
  날짜/작성자: 2026-07-11 / Ju Hyeok

- 결정: 위반 수정 시 CI의 detekt 스텝(`./gradlew detekt --no-daemon`)은 변경하지 않는다. 위반 수집 목적의 로컬 실행에서만 `--continue`를 사용한다.
  근거: CI는 위반이 1건이라도 있으면 실패해야 하는 게이트이므로 fail-fast여도 목적을 달성한다. 다만 로컬에서 전체 위반 목록을 봐야 할 때는 `--continue`가 필요하다는 사실을 이 문서에 기록해 둔다.
  날짜/작성자: 2026-07-11 / Ju Hyeok


## Outcomes & Retrospective


(작업 완료 시 작성)


## Context and Orientation


### 용어 정의

**Detekt**: Kotlin 소스 코드를 정적으로 분석하는 도구. 소스 파일을 PSI 트리로 파싱하고 등록된 Rule들이 각 노드를 방문(visitor pattern)하며 검사한다. 커스텀 룰을 JAR로 패키징하면 Detekt가 ServiceLoader로 자동 발견한다.

**PSI (Program Structure Interface)**: JetBrains의 소스 코드 구문 트리 표현. 타입 정보 없이 소스 텍스트 구조(클래스 이름, 함수 이름, 호출식 텍스트 등)만으로 분석할 수 있다. 이 프로젝트의 모든 커스텀 규칙은 타입 분석(type resolution) 없이 PSI 텍스트 수준에서만 동작한다 — Kotlin 2.x와 detekt 1.23의 K1 타입 분석 호환성 문제를 회피하기 위한 기존 결정이다.

**RuleSetProvider / ServiceLoader**: Detekt가 커스텀 룰을 발견하는 진입점. `quality/detekt-rules/src/main/resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` 파일에 Provider 클래스의 완전한 이름을 한 줄씩 적으면 런타임에 로드된다. Provider의 `ruleSetId`가 `config/detekt/detekt.yml`의 최상위 키가 된다.

**Screen / Content / Component**: 이 프로젝트 Presentation Layer의 3계층. `*Screen`은 ViewModel 구독·SideEffect 처리·navigation 연결만 담당하는 진입점, `*Content`는 `uiState`와 콜백만 받아 Scaffold부터 레이아웃 전체를 그리는 순수 렌더러, Component는 Content 내부에서 조립되는 하위 렌더러다. 상세 근거는 `docs/mvi-presentation-layer-structure.md`와 `docs/screen-content-srp-responsibility.md` 참조.

### 현재 상태

커스텀 detekt 규칙 인프라는 이미 완성되어 있다. `quality/detekt-rules/` 모듈에 규칙과 테스트가 있고, `build-logic/src/main/kotlin/NeveraQualityPlugin.kt`가 모든 대상 모듈에 detekt를 적용하며 config로 `config/detekt/detekt.yml`, 플러그인으로 `:quality:detekt-rules` JAR를 연결한다. CI(`.github/workflows/ci.yml`)는 `./gradlew detekt` 스텝을 실행하고, Claude Code Stop 훅(`.claude/hooks/check-detekt.sh`)도 `.kt` 변경이 있으면 `./gradlew detekt`를 실행해 실패 시 최대 3회까지 자동 수정을 유도한다. 따라서 이 계획에서 CI·훅 파일은 수정할 필요가 없다.

기존 규칙은 두 룰셋에 5개가 있다. `NeveraMviRules`: `NeveraViewModelInheritanceRule`(feature ViewModel의 NeveraViewModel 상속 강제), `ReduceOutsideApplyMutationRule`(reduce 호출 위치 제한), `SealedInterfaceContractRule`(Intent/Mutation/SideEffect의 sealed interface 강제), `ContentComposableParameterRule`(*Content 파라미터를 UiState/함수 타입/Modifier로 제한). `NeveraDesignSystemRules`: `Material3AppBarRule`(Material3 기본 AppBar 사용 금지). 이번 신규 규칙은 이들과 검사 대상이 겹치지 않는다.

수정 대상 위반 화면 5개 파일의 현재 구조:

`feature/auth/src/main/kotlin/com/anddd/nevera/feature/auth/signup/SignupScreen.kt` — 60행 부근에 Screen 함수 밖 private 헬퍼 `showToast`가 있고 그 안에서 `Toast.makeText`를 호출한다.

`feature/ingredient/src/main/kotlin/com/anddd/nevera/feature/ingredient/main/IngredientScreen.kt` — Screen 함수가 직접 `Scaffold`를 호출하고, topBar(`IngredientAppBar`)와 `uiState.phase`(Scanning / ScanSuccess / Registering)별 본문 분기를 Screen 안에 갖고 있다. 취소 확인 다이얼로그(`showCloseConfirm` local state)와 SideEffect 처리(`collectSideEffect`)도 있다. 같은 패키지 `component/IngredientContent.kt`는 현재 ScanSuccess 상태의 리스트 UI만 담당한다.

`feature/ingredient/src/main/kotlin/com/anddd/nevera/feature/ingredient/photodetail/PhotoDetailScreen.kt` — ViewModel 없이 `imageUri: String`을 받아 확대/이동 제스처가 있는 이미지 뷰어를 Screen이 직접 그린다.

`feature/ingredient/src/main/kotlin/com/anddd/nevera/feature/ingredient/registersuccess/RegisterSuccessScreen.kt` — ViewModel 없이 `totalSavedAmount: Int`와 콜백 2개를 받아 완료 화면을 Screen이 직접 그린다.

`feature/notification/src/main/kotlin/com/anddd/nevera/feature/notification/main/NotificationScreen.kt` — Screen은 ViewModel 구독과 권한 체크(`DisposableEffect`)를 하고, 같은 파일의 private `NotificationList`(uiState + `LazyPagingItems` + onIntent를 받는 사실상의 Content)가 Scaffold와 리스트를 그린다. Preview 2개도 이 파일에 있다.


## Plan of Work


### 마일스톤 1: 신규 규칙 구현 및 단위 테스트

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/screencontent/` 패키지를 새로 만들고 다음을 작성한다. 테스트를 먼저 작성하고 구현이 테스트를 통과시키는 순서로 진행한다.

`rules/ScreenDelegatesToContentRule.kt`: `visitNamedFunction`에서 함수 이름이 `Screen`으로 끝나고 `@Composable`이 붙어 있으며 패키지에 `feature`가 포함되면, 함수 본문 전체(`collectDescendantsOfType<KtCallExpression>`)에서 `{접두사}Content` 이름의 호출이 있는지 확인한다. 없으면 보고한다. 접두사 일치를 요구하는 이유는 `LoadingContent()` 같은 공용 컴포넌트 호출로 규칙을 우회하는 것을 막기 위해서다.

`rules/ScreenNoScaffoldRule.kt`: `visitCallExpression`에서 callee 텍스트가 `Scaffold`이고, 파일 이름이 `Screen.kt`로 끝나며, 패키지에 `feature`가 포함되면 보고한다.

`rules/ViewModelAccessOnlyInScreenRule.kt`: callee가 금지 목록(`hiltViewModel`, `viewModel`, `collectAsState`, `collectAsStateWithLifecycle`, `collectSideEffect`, `collectAsLazyPagingItems`)에 있고 패키지에 `feature`가 포함되면, 상위 `KtNamedFunction` 체인을 따라 올라가며 `*Screen` 이름 함수 또는 `@Preview` 함수를 만나는지 확인한다. 만나지 못하면 보고한다. Screen 함수의 파라미터 기본값(`viewModel: HomeViewModel = hiltViewModel()`)도 PSI상 그 함수 소속이므로 자연히 허용된다.

`rules/ToastOutsideScreenRule.kt`: callee가 `makeText`이고 부모가 `Toast.` 수신자를 가진 `KtDotQualifiedExpression`이며 패키지에 `feature`가 포함되면, 상위 함수 체인에 `*Screen` 이름 함수가 없을 때 보고한다.

`NeveraScreenContentRuleSetProvider.kt`: `ruleSetId = "NeveraScreenContentRules"`로 위 4개 규칙을 묶는다.

등록 작업 두 가지: `quality/detekt-rules/src/main/resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider`에 `com.anddd.nevera.quality.screencontent.NeveraScreenContentRuleSetProvider` 한 줄 추가. `config/detekt/detekt.yml` 말미에 `NeveraScreenContentRules: active: true`와 4개 규칙 `active: true` 추가.

기존 규칙 수정 한 가지: `quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/ContentComposableParameterRule.kt`의 `isAllowedParameter`에 `referencedName == "LazyPagingItems"` 허용 분기를 추가하고, 대응 테스트를 `ContentComposableParameterRuleTest.kt`에 추가한다.

테스트는 `quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/screencontent/rules/`에 규칙당 1파일씩 작성한다. 일반 규칙은 `detekt-test`의 `lint(code)` 확장을 쓰고, 파일명이 필요한 `ScreenNoScaffoldRule`만 `io.github.detekt.test.utils.compileContentForTest(code, "HomeScreen.kt")`로 KtFile을 만들어 `rule.lint(ktFile)`로 검사한다. 각 규칙마다 최소한 위반 1건 검출·정상 코드 통과·feature 패키지 아님 제외의 세 케이스를 포함하고, `ViewModelAccessOnlyInScreenRule`에는 Preview 예외 케이스, `ScreenDelegatesToContentRule`에는 공용 Content 우회 차단 케이스, `ScreenNoScaffoldRule`에는 private 하위 컴포저블 우회 차단 케이스를 추가한다.

### 마일스톤 2: 전체 위반 수집

저장소 루트에서 `./gradlew :quality:detekt-rules:test`로 단위 테스트를 통과시킨 뒤 `./gradlew detekt --continue`를 실행한다. fail-fast 문제 때문에 `--continue`가 필수다. 스파이크에서 확인된 6건 외에 추가 위반이 없는지 확인하고, 있다면 이 문서의 `Surprises & Discoveries`와 마일스톤 3의 수정 목록에 반영한다.

### 마일스톤 3: 위반 화면 단계적 수정

수정은 화면당 1커밋을 원칙으로 하고, 각 수정 후 해당 모듈의 detekt를 재실행해 그 화면의 위반이 사라졌는지 확인한다. 커밋은 사용자 확인 후 진행한다.

SignupScreen(auth): private `showToast` 헬퍼를 제거하고 Toast 호출을 `SignupScreen` 함수의 `collectSideEffect` 분기 안으로 인라인한다. 동작 변화 없음.

PhotoDetailScreen(ingredient): `photodetail/model/PhotoDetailUiState.kt`에 `data class PhotoDetailUiState(val imageUri: String)`(NeveraState 구현)를 만들고, `photodetail/component/PhotoDetailContent.kt`로 렌더링 코드(확대/이동 제스처 포함 — 리셋되어도 비즈니스 영향이 없는 local UI state이므로 Content 내부 remember 유지)를 옮긴다. Screen은 `BackHandler`, 시스템바 처리(`DisposableEffect`), navigation 콜백만 남기고 `PhotoDetailContent(uiState = PhotoDetailUiState(imageUri), ...)`를 호출한다. Preview는 Content 파일로 이동한다.

RegisterSuccessScreen(ingredient): 같은 방식으로 `RegisterSuccessUiState(val totalSavedAmount: Int)`와 `component/RegisterSuccessContent.kt`를 만든다. 금액 포맷팅(`NumberFormat`)은 표시 형식이므로 Content로 옮긴다. Screen에는 `BackHandler`와 콜백 전달만 남는다.

IngredientScreen(ingredient): 가장 큰 수정. 기존 `component/IngredientContent.kt`의 리스트 UI를 `component/IngredientListSection.kt`(가칭)로 옮기고, `IngredientContent`를 화면 최상위 렌더러로 재구성한다 — Scaffold, `IngredientAppBar`(Screen 파일에서 이동), phase별 분기(Scanning의 흰 배경 + `OcrScanningDialog`, ScanSuccess/Registering의 리스트 + `LoadingContent`), 취소 확인 다이얼로그(`showCloseConfirm`은 리셋돼도 비즈니스 영향이 없는 순수 UI 상호작용이므로 Content 내부 local state로 이동)를 모두 Content가 담당한다. Screen에는 ViewModel 구독, `collectSideEffect`(navigation·Toast), `IngredientContent(uiState, onIntent)` 호출만 남는다.

NotificationScreen(notification): private `NotificationList`를 `component/NotificationContent.kt`로 옮기고 `NotificationContent`로 개명한다. 파라미터는 `uiState`, `pagingItems: LazyPagingItems<...>`(마일스톤 1의 허용 추가로 통과), `onIntent`, `modifier`. 로딩 오버레이(`LoadingContent`)는 `pagingItems.loadState`로 결정되므로 Content 내부로 옮긴다. Preview 2개도 Content 파일로 이동한다. Screen에는 ViewModel 구독, `collectAsLazyPagingItems`, 권한 체크 `DisposableEffect`, `collectSideEffect`만 남는다.

### 마일스톤 4: 최종 검증 및 문서 역참조

`./gradlew :quality:detekt-rules:test`와 `./gradlew detekt`(플래그 없이 — 이제 전부 통과해야 하므로 fail-fast여도 무방)를 실행해 모두 성공하는지 확인한다. 이후 `docs/mvi-presentation-layer-structure.md` 4장·7장, `docs/screen-content-srp-responsibility.md` 5장·6장에 해당 위반을 자동 검출하는 규칙 ID를 한 줄씩 역참조로 추가한다. CI와 Stop 훅은 이미 `./gradlew detekt`를 실행하므로 수정하지 않고, 편입 사실만 최종 확인한다.


## Concrete Steps


작업 디렉터리는 항상 저장소 루트(`/Users/juhyeok/AndroidStudioProjects/Nevera-Android`), 브랜치는 `feature/detekt-screen-content-boundary`(생성 완료).

    # 마일스톤 1 검증
    ./gradlew :quality:detekt-rules:test
    # 기대: BUILD SUCCESSFUL, 신규 테스트 포함 전체 통과

    # 마일스톤 2: 위반 수집 (--continue 필수)
    ./gradlew detekt --continue 2>&1 | grep -E ':[0-9]+:[0-9]+:.*\[[A-Za-z0-9]+\]$'
    # 기대: 스파이크와 동일한 6건 (auth 1, ingredient 3, notification 2)

    # 마일스톤 3: 화면 수정 후 모듈별 재확인 (예: ingredient)
    ./gradlew :feature:ingredient:detekt
    # 기대: BUILD SUCCESSFUL

    # 마일스톤 4: 최종 검증
    ./gradlew :quality:detekt-rules:test detekt
    # 기대: BUILD SUCCESSFUL (위반 0건)


## Validation and Acceptance


수용 기준은 세 가지다. 첫째, `./gradlew :quality:detekt-rules:test`가 통과하고, 신규 테스트가 각 규칙의 위반 검출과 정상 통과를 모두 증명한다. 둘째, 마일스톤 3 완료 전 `./gradlew detekt --continue`는 정확히 위반 화면들에서 실패하고, 완료 후 `./gradlew detekt`는 성공한다 — 즉 규칙이 실제 위반을 잡았고 수정이 유효했음을 전후 비교로 증명한다. 셋째, 수정된 화면 4곳은 동작 변화가 없어야 한다. 각 화면의 Preview가 렌더링되는지 확인하고, 특히 IngredientScreen은 phase 전환(스캔 중 → 성공 → 등록 중)과 취소 확인 다이얼로그 동작이 기존과 동일한지 코드 리뷰로 확인한다.


## Idempotence and Recovery


마일스톤 1·2는 추가만 하므로 몇 번을 반복해도 안전하다. 마일스톤 3의 화면 수정은 화면당 1커밋으로 분리하므로, 특정 화면 수정이 문제가 되면 해당 커밋만 되돌리고 그 화면의 위반을 임시로 `@Suppress("규칙ID")` 처리한 뒤 후속 작업으로 넘길 수 있다. 규칙 자체를 긴급히 꺼야 하면 `config/detekt/detekt.yml`에서 해당 규칙의 `active: false`로 바꾸는 것만으로 CI·훅 양쪽에서 즉시 비활성화된다.


## Interfaces and Dependencies


새 의존성은 없다. 기존 `quality/detekt-rules/build.gradle.kts`의 `detekt-api`(compileOnly), `detekt-test`·`assertj-core`(testImplementation)를 그대로 사용한다. detekt 버전은 1.23.8(`gradle/libs.versions.toml`).

마일스톤 1 종료 시점에 존재해야 하는 타입: `com.anddd.nevera.quality.screencontent.NeveraScreenContentRuleSetProvider`(RuleSetProvider 구현, ruleSetId `NeveraScreenContentRules`), 같은 패키지 `rules/` 아래 `ScreenDelegatesToContentRule`, `ScreenNoScaffoldRule`, `ViewModelAccessOnlyInScreenRule`, `ToastOutsideScreenRule`(모두 `io.gitlab.arturbosch.detekt.api.Rule` 상속, `Config` 생성자 파라미터).

마일스톤 3 종료 시점에 존재해야 하는 타입: `com.anddd.nevera.feature.ingredient.photodetail.model.PhotoDetailUiState`, `...photodetail.component.PhotoDetailContent`, `...registersuccess.model.RegisterSuccessUiState`(또는 파라미터 구조에 맞는 동등물), `...registersuccess.component.RegisterSuccessContent`, `com.anddd.nevera.feature.notification.main.component.NotificationContent`, 재구성된 `com.anddd.nevera.feature.ingredient.main.component.IngredientContent`.
