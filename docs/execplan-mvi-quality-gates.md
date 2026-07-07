# MVI 패턴 품질 게이트 구축 (Detekt Custom Rules + CI)

이 ExecPlan은 살아있는 문서다. `Progress`, `Surprises & Discoveries`, `Decision Log`, `Outcomes & Retrospective` 섹션은 작업이 진행되는 동안 반드시 최신 상태로 유지해야 한다.

이 문서는 저장소 루트의 `PLANS.md`에 정의된 규칙에 따라 작성되고 유지되어야 한다.


## Purpose / Big Picture


Nevera Android 프로젝트는 feature 모듈의 Presentation Layer에서 MVI(Model-View-Intent) 패턴을 표준으로 사용하고, 디자인 시스템 컴포넌트를 통해 UI 일관성을 강제한다. 이 두 규칙 체계(MVI 패턴 규칙, 디자인 시스템 규칙)는 현재 `CLAUDE.md`와 `docs/mvi-core-architecture.md`에 문서화되어 있지만, 코드 작성 시 자동으로 검증되지는 않는다.

이 ExecPlan을 완료하면 두 가지가 가능해진다. 첫째, 로컬에서 `./gradlew detekt`를 실행하면 MVI 패턴 위반과 디자인 시스템 위반을 즉시 확인할 수 있다. 둘째, PR을 올리면 CI가 Detekt를 실행하여 위반이 있을 경우 빌드를 실패시킨다. 이를 통해 AI 에이전트가 작성하든 사람이 작성하든 feature 모듈 코드가 동일한 구조 규칙을 따르는지 자동으로 검증된다.

MVI 규칙과 디자인 시스템 규칙은 `RuleSetProvider`와 config 파일을 각각 분리하여 관리한다. 이를 통해 두 영역의 규칙을 독립적으로 활성화/비활성화하고, 각 파일이 단일 책임을 갖도록 구성한다.


## Progress


- [x] 마일스톤 1: Detekt 기본 설치 및 설정 — `:quality:detekt-rules` 모듈 생성, `nevera.quality` Convention Plugin 작성, feature 모듈에 Detekt 적용
- [x] 마일스톤 2: MVI 패턴 규칙 구현 — `NeveraViewModelInheritanceRule`, `ReduceOutsideApplyMutationRule`, `SealedInterfaceContractRule`, `ContentComposableParameterRule` 구현 및 테스트
- [ ] 마일스톤 3: 디자인 시스템 규칙 구현 — `Material3AppBarRule` 구현 및 테스트 (향후 확장 기반 마련)
- [ ] 마일스톤 4: CI 통합 — `.github/workflows/ci.yml`에 Detekt 스텝 추가, 빌드 실패 조건 연결


## Surprises & Discoveries


- **detekt-gradlePlugin은 `implementation`으로 선언해야 한다**: Convention Plugin(`NeveraQualityPlugin`)은 Gradle 데몬이 플러그인 클래스를 로드할 때 `configure<DetektExtension>` 호출에 필요한 `DetektExtension` 클래스가 런타임 클래스패스에 있어야 한다. `compileOnly`는 컴파일 시점에만 클래스를 제공하므로, 런타임에 `Could not generate a decorated class for type NeveraQualityPlugin` 오류가 발생한다. Android Gradle Plugin, Kotlin Gradle Plugin 등은 Gradle 데몬이 이미 로드해 두기 때문에 `compileOnly`로도 동작하지만, Detekt는 그렇지 않다. ExecPlan의 단계 1-4 설명은 `compileOnly`로 잘못 기술되어 있다(실제 구현은 `implementation` 사용). 날짜: 2026-07-07

- **`formatting` 룰셋 키는 `detekt-formatting` 플러그인 없이 사용할 수 없다**: `config.validation: true` 상태에서 `formatting: active: false`를 config에 포함하면 `Property 'formatting' is misspelled or does not exist` 오류가 발생한다. `detekt-formatting`은 ktlint 기반의 별도 Detekt 플러그인이며, 사용하지 않는다면 config 파일에서 해당 키를 제거해야 한다. 날짜: 2026-07-07

- **`detekt-test`는 AssertJ를 포함하지 않는다**: ExecPlan의 테스트 코드에서 `assertThat`은 AssertJ에서 가져온다. 그러나 `detekt-test`는 AssertJ를 transitive dependency로 공개하지 않으므로, `testImplementation(libs.assertj.core)` 를 `build.gradle.kts`에 별도로 추가해야 한다. 날짜: 2026-07-07


## Decision Log


- 결정: Android Lint 대신 Detekt를 사용한다.
  근거: Detekt는 Kotlin-first 정적 분석 도구로, PSI(JetBrains Program Structure Interface) 기반으로 Kotlin 소스 파일을 직접 분석한다. Android Lint는 Android API 오용 탐지에 특화되어 있고 커스텀 룰 API가 복잡하다. Detekt는 커스텀 룰 작성 API가 직관적이며, IntelliJ/Android Studio 플러그인과도 연동된다.
  날짜/작성자: 2026-07-07 / Ju Hyeok

- 결정: 타입 분석(type resolution)이 필요 없는 텍스트 기반 PSI 분석만 사용한다.
  근거: 프로젝트의 Kotlin 버전은 2.3.0이다. Detekt 1.23.x는 타입 분석 시 K1 프론트엔드를 사용하는데, Kotlin 2.x 프로젝트에서 이를 사용하면 설정이 복잡해질 수 있다. 우리가 강제할 규칙(슈퍼타입 이름 확인, sealed 여부, 파라미터 타입 이름 등)은 모두 소스 텍스트 수준에서 PSI 노드의 이름(name)과 구조(structure)만으로 확인 가능하다. 따라서 타입 분석 없이도 모든 규칙을 구현할 수 있어 호환성 문제를 회피한다.
  날짜/작성자: 2026-07-07 / Ju Hyeok

- 결정: 커스텀 룰 모듈을 `:quality:detekt-rules`로 분리한다.
  근거: `build-logic/`은 Gradle Convention Plugin 전용이다. Detekt 룰은 Detekt가 런타임에 로드하는 별도 JAR이므로, 일반 Gradle 모듈로 분리하는 것이 관심사 분리 원칙에 맞다.
  날짜/작성자: 2026-07-07 / Ju Hyeok

- 결정: MVI 패턴 규칙과 디자인 시스템 규칙을 별도 `RuleSetProvider`와 config 파일로 분리한다.
  근거: 두 규칙 체계는 서로 다른 영역을 담당한다. MVI 규칙(`NeveraMviRules`)은 데이터 흐름 아키텍처 준수를 검증하고, 디자인 시스템 규칙(`NeveraDesignSystemRules`)은 UI 컴포넌트 일관성을 검증한다. 하나의 `RuleSetProvider`와 하나의 config 파일에 두 영역을 혼합하면 각 파일이 단일 책임을 갖지 못한다. 분리하면 특정 영역만 독립적으로 활성화/비활성화할 수 있고, 향후 각 영역에 규칙이 추가될 때 구조가 명확하게 유지된다. CI 스텝은 단일 `./gradlew detekt`로 유지한다(어느 쪽 위반이든 PR은 동일하게 차단해야 하므로 분리의 실익이 없다).
  날짜/작성자: 2026-07-07 / Ju Hyeok


## Outcomes & Retrospective


(완료 후 작성한다.)


## Context and Orientation


### 용어 정의

**Detekt**: Kotlin 소스 코드를 정적으로 분석하는 도구. 소스 파일을 PSI 트리로 파싱하고 등록된 Rule들을 각 노드에 방문하는 방식으로 동작한다. 커스텀 룰을 JAR로 패키징하면 Detekt가 ServiceLoader로 자동 발견한다.

**PSI (Program Structure Interface)**: JetBrains가 IntelliJ IDEA를 위해 개발한 소스 코드 구문 트리 표현. Detekt는 이 PSI 트리를 순회(visitor pattern)하며 각 노드(클래스, 함수, 호출식 등)를 검사한다. 타입 정보 없이 소스 텍스트 구조만으로 분석할 수 있다.

**RuleSetProvider**: Detekt가 ServiceLoader로 로드하는 진입점 인터페이스. 하나의 Provider가 여러 Rule을 묶어 하나의 RuleSet으로 제공한다. `ruleSetId`로 구분되며, 이 값이 Detekt config 파일의 최상위 키로 사용된다.

**ServiceLoader**: Java 표준 메커니즘으로, JAR 내 `META-INF/services/<인터페이스명>` 파일에 구현체 클래스명을 줄 단위로 적어두면 런타임에 자동으로 발견한다. Detekt는 이 방식으로 외부 JAR에서 RuleSetProvider를 찾는다. 여러 Provider를 등록하려면 각 클래스명을 별도 줄에 추가한다.

**Convention Plugin**: Gradle 멀티 모듈 프로젝트에서 여러 모듈에 공통 설정을 재사용하기 위한 플러그인. 이 프로젝트의 `build-logic/` 디렉토리에 위치하며, `nevera.feature` 플러그인은 모든 feature 모듈에 자동 적용된다.

**MVI (Model-View-Intent)**: 단방향 데이터 흐름 패턴. 사용자 액션은 Intent → ViewModel.handleIntent() → (UseCase) → Mutation → applyMutation() → reduce() → UiState 순서로 흐른다. 이 흐름을 벗어나는 코드를 Detekt로 탐지한다.

### 현재 프로젝트 구조

    Nevera-Android/
    ├── PLANS.md                         ← ExecPlan 작성 규칙 (이 문서가 따르는 메타 문서)
    ├── build-logic/                     ← Gradle Convention Plugin 모음
    │   ├── build.gradle.kts             ← 플러그인 등록 (kotlin-dsl 기반)
    │   └── src/main/kotlin/
    │       ├── NeveraFeaturePlugin.kt   ← 모든 feature 모듈에 자동 적용
    │       └── NeveraKotlinJvmPlugin.kt ← 순수 JVM Kotlin 모듈에 적용
    ├── core/mvi/                        ← MVI 기반 타입 정의
    │   └── src/main/kotlin/com/anddd/nevera/core/mvi/
    │       ├── NeveraViewModel.kt       ← feature ViewModel이 상속해야 할 추상 클래스
    │       └── NeveraContracts.kt       ← NeveraState, NeveraSideEffect, NeveraIntent, NeveraMutation 인터페이스
    ├── feature/                         ← 화면 모듈 (main, auth, fridge, ingredient, mypage 등)
    ├── gradle/libs.versions.toml        ← 버전 카탈로그 (의존성 버전 중앙 관리)
    ├── settings.gradle.kts              ← 모든 모듈 등록
    └── .github/workflows/ci.yml        ← CI: unit test + lintDebug 실행

### 완료 후 추가될 구조

    Nevera-Android/
    ├── config/detekt/
    │   ├── detekt-mvi.yml               ← MVI 패턴 룰셋 설정
    │   └── detekt-designsystem.yml      ← 디자인 시스템 룰셋 설정
    └── quality/detekt-rules/
        ├── build.gradle.kts
        └── src/
            ├── main/
            │   ├── kotlin/com/anddd/nevera/quality/
            │   │   ├── mvi/                                    ← MVI 패턴 전용 영역
            │   │   │   ├── NeveraMviRuleSetProvider.kt
            │   │   │   └── rules/
            │   │   │       ├── NeveraViewModelInheritanceRule.kt
            │   │   │       ├── ReduceOutsideApplyMutationRule.kt
            │   │   │       ├── SealedInterfaceContractRule.kt
            │   │   │       └── ContentComposableParameterRule.kt
            │   │   └── designsystem/                           ← 디자인 시스템 전용 영역
            │   │       ├── NeveraDesignSystemRuleSetProvider.kt
            │   │       └── rules/
            │   │           └── Material3AppBarRule.kt
            │   └── resources/META-INF/services/
            │       └── io.gitlab.arturbosch.detekt.api.RuleSetProvider
            └── test/
                └── kotlin/com/anddd/nevera/quality/
                    ├── mvi/rules/
                    │   ├── NeveraViewModelInheritanceRuleTest.kt
                    │   ├── ReduceOutsideApplyMutationRuleTest.kt
                    │   ├── SealedInterfaceContractRuleTest.kt
                    │   └── ContentComposableParameterRuleTest.kt
                    └── designsystem/rules/
                        └── Material3AppBarRuleTest.kt

### 강제할 규칙 목록

**MVI 패턴 규칙** (`NeveraMviRules` 룰셋, `config/detekt/detekt-mvi.yml`로 관리):

1. **NeveraViewModel 상속 강제**: `feature/*` 패키지의 `*ViewModel` 클래스는 반드시 `NeveraViewModel`을 상속해야 한다. `ViewModel()`을 직접 상속하면 위반이다.

2. **reduce() 단일 호출 지점 강제**: `reduce { }` 블록은 `applyMutation()` 함수 내부에서만 호출해야 한다. 다른 함수 내부에서 직접 호출하면 위반이다.

3. **Contract sealed interface 강제**: `*Intent`, `*Mutation`, `*SideEffect`라는 이름의 타입은 반드시 `sealed interface`로 선언해야 한다. `class`, `sealed class`, `interface`(비sealed)로 선언하면 위반이다.

4. **Content Composable 파라미터 제약**: `*Content`라는 이름의 `@Composable` 함수는 `*UiState` 타입, 함수 타입(`(*) -> Unit`), `Modifier` 타입 파라미터만 허용한다. local state 값이나 Boolean 플래그 등을 파라미터로 받으면 위반이다.

**디자인 시스템 규칙** (`NeveraDesignSystemRules` 룰셋, `config/detekt/detekt-designsystem.yml`로 관리):

5. **Material3 기본 AppBar 금지**: `TopAppBar`, `CenterAlignedTopAppBar`, `SmallTopAppBar`, `MediumTopAppBar`, `LargeTopAppBar`를 직접 호출하면 위반이다. 디자인 시스템의 `NeveraAppBar` 계열을 사용해야 한다.


## Plan of Work


### 마일스톤 1: Detekt 기본 설치 및 설정

이 마일스톤이 끝나면 `./gradlew :feature:main:detekt`가 오류 없이 실행된다. 아직 커스텀 룰은 없지만 Detekt 인프라가 완성된다.

**1-1. libs.versions.toml에 Detekt 항목 추가**

`gradle/libs.versions.toml`의 `[versions]` 섹션에 추가한다.

    detekt = "1.23.8"

`[libraries]` 섹션에 추가한다.

    detekt-api = { group = "io.gitlab.arturbosch.detekt", name = "detekt-api", version.ref = "detekt" }
    detekt-test = { group = "io.gitlab.arturbosch.detekt", name = "detekt-test", version.ref = "detekt" }
    detekt-gradlePlugin = { group = "io.gitlab.arturbosch.detekt", name = "detekt-gradle-plugin", version.ref = "detekt" }

`[plugins]` 섹션에 추가한다.

    detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }

**1-2. quality/detekt-rules 모듈 생성**

`quality/detekt-rules/build.gradle.kts`를 생성한다. `nevera.kotlin.jvm`은 이미 프로젝트에 존재하는 Convention Plugin으로, 순수 JVM Kotlin 모듈 기본 설정을 적용한다.

    plugins {
        id("nevera.kotlin.jvm")
    }

    dependencies {
        compileOnly(libs.detekt.api)
        testImplementation(libs.detekt.test)
    }

**1-3. settings.gradle.kts에 모듈 등록**

`settings.gradle.kts`의 기존 `include(...)` 목록 끝에 추가한다.

    include(":quality:detekt-rules")

**1-4. Detekt Convention Plugin 생성**

`build-logic/src/main/kotlin/NeveraQualityPlugin.kt`를 생성한다. 이 플러그인은 MVI와 디자인 시스템 두 config 파일을 모두 참조하여 단일 `./gradlew detekt` 태스크로 두 룰셋을 함께 검사한다.

    package com.anddd.nevera.buildlogic

    import io.gitlab.arturbosch.detekt.extensions.DetektExtension
    import org.gradle.api.Plugin
    import org.gradle.api.Project
    import org.gradle.kotlin.dsl.configure
    import org.gradle.kotlin.dsl.dependencies

    class NeveraQualityPlugin : Plugin<Project> {
        override fun apply(target: Project) {
            with(target) {
                pluginManager.apply("io.gitlab.arturbosch.detekt")

                configure<DetektExtension> {
                    config.setFrom(
                        rootProject.files("config/detekt/detekt-mvi.yml"),
                        rootProject.files("config/detekt/detekt-designsystem.yml"),
                    )
                    buildUponDefaultConfig = true
                    parallel = true
                }

                dependencies {
                    "detektPlugins"(project(":quality:detekt-rules"))
                }
            }
        }
    }

`build-logic/build.gradle.kts`의 `dependencies` 블록에 Detekt Gradle Plugin 의존성을 추가한다. `compileOnly` 스코프로 추가해야 Convention Plugin 소스에서 `DetektExtension`을 import할 수 있다.

    compileOnly(libs.detekt.gradlePlugin)

`build-logic/build.gradle.kts`의 `gradlePlugin` 블록에 플러그인을 등록한다.

    register("neveraQuality") {
        id = "nevera.quality"
        implementationClass = "com.anddd.nevera.buildlogic.NeveraQualityPlugin"
    }

**1-5. NeveraFeaturePlugin에 nevera.quality 적용**

`build-logic/src/main/kotlin/NeveraFeaturePlugin.kt`의 `apply()` 함수 내부에 다음 한 줄을 추가한다.

    pluginManager.apply("nevera.quality")

이로써 `nevera.feature` 플러그인을 사용하는 모든 feature 모듈에 Detekt가 자동으로 적용된다.

**1-6. config 파일 생성 — MVI 패턴 룰셋**

`config/detekt/detekt-mvi.yml`을 생성한다. 이 파일은 MVI 패턴 룰셋만 담당한다. Detekt 기본 룰셋은 모두 비활성화하여 커스텀 룰 검증에 집중한다.

    config:
      validation: true
      warningsAsErrors: true

    comments:
      active: false
    complexity:
      active: false
    coroutines:
      active: false
    empty-blocks:
      active: false
    exceptions:
      active: false
    formatting:
      active: false
    naming:
      active: false
    performance:
      active: false
    potential-bugs:
      active: false
    style:
      active: false

    NeveraMviRules:
      active: true
      NeveraViewModelInheritanceRule:
        active: true
      ReduceOutsideApplyMutationRule:
        active: true
      SealedInterfaceContractRule:
        active: true
      ContentComposableParameterRule:
        active: true

**1-7. config 파일 생성 — 디자인 시스템 룰셋**

`config/detekt/detekt-designsystem.yml`을 생성한다. 이 파일은 디자인 시스템 룰셋만 담당한다.

    NeveraDesignSystemRules:
      active: true
      Material3AppBarRule:
        active: true

**1-8. 마일스톤 1 검증**

프로젝트 루트에서 실행한다.

    ./gradlew :quality:detekt-rules:build
    ./gradlew :feature:main:detekt

두 명령 모두 BUILD SUCCESSFUL이 출력되면 마일스톤 1이 완료된 것이다. 커스텀 룰 파일들은 아직 빈 스텁 상태여도 빌드는 통과해야 한다.


### 마일스톤 2: MVI 패턴 규칙 구현

이 마일스톤이 끝나면 NeveraViewModel 상속 위반, reduce() 위치 위반, sealed interface 선언 누락, Content Composable 파라미터 위반을 Detekt가 탐지하고 보고한다. 각 규칙은 테스트로 검증한다.

**Detekt Rule 작성 방식 설명**

모든 커스텀 룰은 `io.gitlab.arturbosch.detekt.api.Rule`을 상속한다. Rule은 PSI 노드 방문자(visitor)다. Kotlin 소스를 파싱하면 클래스(`KtClass`), 함수(`KtNamedFunction`), 호출식(`KtCallExpression`) 등의 노드로 구성된 트리가 만들어진다. Rule은 이 트리에서 원하는 노드를 오버라이드해 검사한다.

위반을 발견하면 `report(CodeSmell(issue, Entity.from(node), message))` 형태로 보고한다. `issue`는 Rule 클래스에 `override val issue: Issue`로 정의한다.

RuleSetProvider는 Rule들을 묶어 Detekt에 등록한다. ServiceLoader가 `META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` 파일에서 클래스명을 읽어 자동으로 로드한다.

**2-1. ServiceLoader 등록 파일 생성**

`quality/detekt-rules/src/main/resources/META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` 파일을 생성한다. 두 Provider를 각각 한 줄씩 등록한다.

    com.anddd.nevera.quality.mvi.NeveraMviRuleSetProvider
    com.anddd.nevera.quality.designsystem.NeveraDesignSystemRuleSetProvider

**2-2. NeveraMviRuleSetProvider 구현**

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/NeveraMviRuleSetProvider.kt`:

    package com.anddd.nevera.quality.mvi

    import com.anddd.nevera.quality.mvi.rules.ContentComposableParameterRule
    import com.anddd.nevera.quality.mvi.rules.NeveraViewModelInheritanceRule
    import com.anddd.nevera.quality.mvi.rules.ReduceOutsideApplyMutationRule
    import com.anddd.nevera.quality.mvi.rules.SealedInterfaceContractRule
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.RuleSet
    import io.gitlab.arturbosch.detekt.api.RuleSetProvider

    class NeveraMviRuleSetProvider : RuleSetProvider {
        override val ruleSetId = "NeveraMviRules"

        override fun instance(config: Config) = RuleSet(
            ruleSetId,
            listOf(
                NeveraViewModelInheritanceRule(config),
                ReduceOutsideApplyMutationRule(config),
                SealedInterfaceContractRule(config),
                ContentComposableParameterRule(config),
            )
        )
    }

**2-3. NeveraViewModelInheritanceRule 구현**

이 룰은 feature 모듈의 ViewModel이 `NeveraViewModel`을 상속하지 않을 때 오류를 낸다.

검사 로직: 클래스 이름이 `ViewModel`로 끝나고, 슈퍼타입 목록에 `NeveraViewModel`이 없으면 위반이다. 패키지 경로에 `feature`가 포함된 경우만 검사한다(`core/mvi`의 `NeveraViewModel` 자체를 검사 대상에서 제외하기 위함).

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/NeveraViewModelInheritanceRule.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.CodeSmell
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.Debt
    import io.gitlab.arturbosch.detekt.api.Entity
    import io.gitlab.arturbosch.detekt.api.Issue
    import io.gitlab.arturbosch.detekt.api.Rule
    import io.gitlab.arturbosch.detekt.api.Severity
    import org.jetbrains.kotlin.psi.KtClass

    class NeveraViewModelInheritanceRule(config: Config) : Rule(config) {

        override val issue = Issue(
            id = "NeveraViewModelInheritanceRule",
            severity = Severity.Defect,
            description = "feature 모듈의 ViewModel은 반드시 NeveraViewModel을 상속해야 한다.",
            debt = Debt.FIVE_MINS,
        )

        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)

            val name = klass.name ?: return
            if (!name.endsWith("ViewModel")) return

            val packageName = klass.containingKtFile.packageFqName.asString()
            if (!packageName.contains("feature")) return

            val superTypeNames = klass.superTypeListEntries
                .mapNotNull { it.typeAsUserType?.referencedName }

            if ("NeveraViewModel" !in superTypeNames) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(klass),
                        "$name 은 NeveraViewModel을 상속해야 한다. ViewModel()을 직접 상속하지 않는다.",
                    )
                )
            }
        }
    }

**2-4. ReduceOutsideApplyMutationRule 구현**

이 룰은 `reduce { }` 호출이 `applyMutation` 함수 바깥에 있을 때 오류를 낸다.

검사 로직: `KtCallExpression`의 `calleeExpression` 텍스트가 `reduce`이고, 해당 호출을 감싸는 가장 가까운 `KtNamedFunction`의 이름이 `applyMutation`이 아니면 위반이다.

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/ReduceOutsideApplyMutationRule.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.CodeSmell
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.Debt
    import io.gitlab.arturbosch.detekt.api.Entity
    import io.gitlab.arturbosch.detekt.api.Issue
    import io.gitlab.arturbosch.detekt.api.Rule
    import io.gitlab.arturbosch.detekt.api.Severity
    import org.jetbrains.kotlin.psi.KtCallExpression
    import org.jetbrains.kotlin.psi.KtNamedFunction
    import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

    class ReduceOutsideApplyMutationRule(config: Config) : Rule(config) {

        override val issue = Issue(
            id = "ReduceOutsideApplyMutationRule",
            severity = Severity.Defect,
            description = "reduce { }는 applyMutation() 내부에서만 호출해야 한다.",
            debt = Debt.FIVE_MINS,
        )

        override fun visitCallExpression(expression: KtCallExpression) {
            super.visitCallExpression(expression)

            val callee = expression.calleeExpression?.text ?: return
            if (callee != "reduce") return

            val enclosingFunction = expression.getParentOfType<KtNamedFunction>(strict = true)
            if (enclosingFunction?.name != "applyMutation") {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        "reduce { }는 applyMutation() 내부에서만 호출할 수 있다. " +
                            "현재 위치: ${enclosingFunction?.name ?: "알 수 없는 함수"}",
                    )
                )
            }
        }
    }

**2-5. SealedInterfaceContractRule 구현**

이 룰은 `*Intent`, `*Mutation`, `*SideEffect` 타입이 `sealed interface`로 선언되지 않으면 오류를 낸다.

검사 로직: 클래스/인터페이스 이름이 `Intent`, `Mutation`, `SideEffect` 중 하나로 끝나고, `sealed`이면서 `interface`인지 확인한다.

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/SealedInterfaceContractRule.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.CodeSmell
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.Debt
    import io.gitlab.arturbosch.detekt.api.Entity
    import io.gitlab.arturbosch.detekt.api.Issue
    import io.gitlab.arturbosch.detekt.api.Rule
    import io.gitlab.arturbosch.detekt.api.Severity
    import org.jetbrains.kotlin.psi.KtClass

    class SealedInterfaceContractRule(config: Config) : Rule(config) {

        private val contractSuffixes = listOf("Intent", "Mutation", "SideEffect")

        override val issue = Issue(
            id = "SealedInterfaceContractRule",
            severity = Severity.Defect,
            description = "*Intent, *Mutation, *SideEffect 타입은 반드시 sealed interface로 선언해야 한다.",
            debt = Debt.FIVE_MINS,
        )

        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)

            val name = klass.name ?: return
            val matchedSuffix = contractSuffixes.firstOrNull { name.endsWith(it) } ?: return

            val isSealed = klass.isSealed()
            val isInterface = klass.isInterface()

            if (!isSealed || !isInterface) {
                val actual = when {
                    isSealed && !isInterface -> "sealed class"
                    !isSealed && isInterface -> "interface"
                    !isSealed && !isInterface -> "class"
                    else -> "알 수 없는 형태"
                }
                report(
                    CodeSmell(
                        issue,
                        Entity.from(klass),
                        "$name (접미사: $matchedSuffix)은 sealed interface로 선언해야 한다. 현재: $actual",
                    )
                )
            }
        }
    }

**2-6. ContentComposableParameterRule 구현**

이 룰은 `*Content`라는 이름의 `@Composable` 함수가 허용되지 않는 파라미터를 가질 때 오류를 낸다. 이 규칙은 MVI의 Screen/Content 분리 원칙을 강제한다. Content는 UiState만으로 렌더링을 결정해야 하므로, local state 값이나 외부에서 주입되는 플래그를 파라미터로 받으면 안 된다.

허용 파라미터 조건:
- 타입 이름이 `UiState`로 끝나는 것 (예: `HomeUiState`)
- PSI에서 `KtFunctionType`으로 나타나는 함수 타입 (예: `(HomeIntent) -> Unit`)
- `Modifier` 타입 (Compose 표준 레이아웃 수정자)

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/mvi/rules/ContentComposableParameterRule.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.CodeSmell
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.Debt
    import io.gitlab.arturbosch.detekt.api.Entity
    import io.gitlab.arturbosch.detekt.api.Issue
    import io.gitlab.arturbosch.detekt.api.Rule
    import io.gitlab.arturbosch.detekt.api.Severity
    import org.jetbrains.kotlin.psi.KtFunctionType
    import org.jetbrains.kotlin.psi.KtNamedFunction
    import org.jetbrains.kotlin.psi.KtParameter
    import org.jetbrains.kotlin.psi.KtUserType

    class ContentComposableParameterRule(config: Config) : Rule(config) {

        override val issue = Issue(
            id = "ContentComposableParameterRule",
            severity = Severity.Defect,
            description = "*Content Composable의 파라미터는 *UiState, 함수 타입, Modifier만 허용된다.",
            debt = Debt.TEN_MINS,
        )

        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)

            val name = function.name ?: return
            if (!name.endsWith("Content")) return

            val hasComposableAnnotation = function.annotationEntries
                .any { it.shortName?.asString() == "Composable" }
            if (!hasComposableAnnotation) return

            for (param in function.valueParameters) {
                if (!isAllowedParameter(param)) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(param),
                            "$name 의 파라미터 '${param.name}'은 허용되지 않는다. " +
                                "*Content Composable은 *UiState, 함수 타입(onIntent), Modifier만 받을 수 있다. " +
                                "local state 값은 Screen에서 관리해야 한다.",
                        )
                    )
                }
            }
        }

        private fun isAllowedParameter(param: KtParameter): Boolean {
            val typeRef = param.typeReference ?: return true
            val typeElement = typeRef.typeElement

            return when {
                typeElement is KtFunctionType -> true
                typeElement is KtUserType &&
                    typeElement.referencedName?.endsWith("UiState") == true -> true
                typeElement is KtUserType &&
                    typeElement.referencedName == "Modifier" -> true
                else -> false
            }
        }
    }

**2-7. 마일스톤 2 테스트 작성**

Detekt의 테스트 유틸리티는 `io.gitlab.arturbosch.detekt.test.lint(code)` 함수를 제공한다. 이 함수는 Kotlin 소스 코드 문자열을 받아 Rule을 실행하고 `List<Finding>`을 반환한다. 리스트가 비어 있으면 위반 없음, 비어 있지 않으면 위반 발견이다.

`quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/mvi/rules/NeveraViewModelInheritanceRuleTest.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.test.lint
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test

    class NeveraViewModelInheritanceRuleTest {

        private val rule = NeveraViewModelInheritanceRule(Config.empty)

        @Test
        fun `NeveraViewModel을 상속하면 위반 없음`() {
            val code = """
                package com.anddd.nevera.feature.main
                class HomeViewModel : NeveraViewModel<HomeUiState, HomeSideEffect, HomeIntent, HomeMutation>(HomeUiState())
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `ViewModel을 직접 상속하면 위반`() {
            val code = """
                package com.anddd.nevera.feature.main
                class HomeViewModel : ViewModel()
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }

        @Test
        fun `feature 패키지 외부의 ViewModel은 검사하지 않음`() {
            val code = """
                package com.anddd.nevera.core.mvi
                abstract class NeveraViewModel<STATE, SIDE_EFFECT, INTENT, MUTATION> : ViewModel()
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }
    }

`quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/mvi/rules/ReduceOutsideApplyMutationRuleTest.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.test.lint
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test

    class ReduceOutsideApplyMutationRuleTest {

        private val rule = ReduceOutsideApplyMutationRule(Config.empty)

        @Test
        fun `applyMutation 안에서 reduce 호출은 위반 없음`() {
            val code = """
                suspend fun applyMutation(mutation: HomeMutation) {
                    when (mutation) {
                        HomeMutation.Loading -> reduce { state.copy(isLoading = true) }
                    }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `applyMutation 밖에서 reduce 호출은 위반`() {
            val code = """
                private fun onRefreshClicked() = intent {
                    reduce { state.copy(isLoading = true) }
                }
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }
    }

`quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/mvi/rules/SealedInterfaceContractRuleTest.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.test.lint
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test

    class SealedInterfaceContractRuleTest {

        private val rule = SealedInterfaceContractRule(Config.empty)

        @Test
        fun `sealed interface Intent는 위반 없음`() {
            assertThat(rule.lint("sealed interface HomeIntent : NeveraIntent")).isEmpty()
        }

        @Test
        fun `sealed class Intent는 위반`() {
            assertThat(rule.lint("sealed class HomeIntent : NeveraIntent()")).hasSize(1)
        }

        @Test
        fun `일반 interface Intent는 위반`() {
            assertThat(rule.lint("interface HomeIntent : NeveraIntent")).hasSize(1)
        }

        @Test
        fun `sealed interface Mutation은 위반 없음`() {
            assertThat(rule.lint("sealed interface HomeMutation : NeveraMutation")).isEmpty()
        }

        @Test
        fun `sealed interface SideEffect는 위반 없음`() {
            assertThat(rule.lint("sealed interface HomeSideEffect : NeveraSideEffect")).isEmpty()
        }
    }

`quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/mvi/rules/ContentComposableParameterRuleTest.kt`:

    package com.anddd.nevera.quality.mvi.rules

    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.test.lint
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test

    class ContentComposableParameterRuleTest {

        private val rule = ContentComposableParameterRule(Config.empty)

        @Test
        fun `UiState와 함수 타입 파라미터만 있으면 위반 없음`() {
            val code = """
                @Composable
                fun HomeContent(
                    uiState: HomeUiState,
                    onIntent: (HomeIntent) -> Unit,
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `Modifier 파라미터는 허용`() {
            val code = """
                @Composable
                fun HomeContent(
                    uiState: HomeUiState,
                    onIntent: (HomeIntent) -> Unit,
                    modifier: Modifier = Modifier,
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `Boolean 파라미터는 위반`() {
            val code = """
                @Composable
                fun HomeContent(
                    uiState: HomeUiState,
                    showBottomSheet: Boolean,
                    onIntent: (HomeIntent) -> Unit,
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }

        @Test
        fun `Composable 어노테이션 없는 Content 함수는 검사하지 않음`() {
            val code = """
                fun HomeContent(
                    uiState: HomeUiState,
                    someFlag: Boolean,
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }
    }

**2-8. 마일스톤 2 검증**

    ./gradlew :quality:detekt-rules:test

모든 테스트가 PASSED이면 마일스톤 2 완료다.

    ./gradlew :feature:main:detekt

현재 코드에 위반이 없으면 BUILD SUCCESSFUL이다.


### 마일스톤 3: 디자인 시스템 규칙 구현

이 마일스톤이 끝나면 Material3 기본 AppBar 직접 사용을 탐지한다. 디자인 시스템 규칙셋(`NeveraDesignSystemRules`)의 기반이 되므로, 향후 색상 토큰 오용, 타이포그래피 토큰 오용 등 새 규칙은 이 패키지와 Provider에 추가하면 된다.

**3-1. NeveraDesignSystemRuleSetProvider 구현**

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/designsystem/NeveraDesignSystemRuleSetProvider.kt`:

    package com.anddd.nevera.quality.designsystem

    import com.anddd.nevera.quality.designsystem.rules.Material3AppBarRule
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.RuleSet
    import io.gitlab.arturbosch.detekt.api.RuleSetProvider

    class NeveraDesignSystemRuleSetProvider : RuleSetProvider {
        override val ruleSetId = "NeveraDesignSystemRules"

        override fun instance(config: Config) = RuleSet(
            ruleSetId,
            listOf(
                Material3AppBarRule(config),
            )
        )
    }

**3-2. Material3AppBarRule 구현**

이 룰은 Material3의 기본 AppBar 컴포넌트를 직접 호출하면 오류를 낸다. Nevera의 Scaffold `topBar`에는 반드시 디자인 시스템의 `NeveraAppBar` 계열을 사용해야 한다.

금지 대상: `TopAppBar`, `CenterAlignedTopAppBar`, `SmallTopAppBar`, `MediumTopAppBar`, `LargeTopAppBar`

`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/designsystem/rules/Material3AppBarRule.kt`:

    package com.anddd.nevera.quality.designsystem.rules

    import io.gitlab.arturbosch.detekt.api.CodeSmell
    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.api.Debt
    import io.gitlab.arturbosch.detekt.api.Entity
    import io.gitlab.arturbosch.detekt.api.Issue
    import io.gitlab.arturbosch.detekt.api.Rule
    import io.gitlab.arturbosch.detekt.api.Severity
    import org.jetbrains.kotlin.psi.KtCallExpression

    class Material3AppBarRule(config: Config) : Rule(config) {

        private val forbiddenAppBars = setOf(
            "TopAppBar",
            "CenterAlignedTopAppBar",
            "SmallTopAppBar",
            "MediumTopAppBar",
            "LargeTopAppBar",
        )

        override val issue = Issue(
            id = "Material3AppBarRule",
            severity = Severity.Defect,
            description = "Material3 기본 AppBar 대신 디자인 시스템의 NeveraAppBar 계열을 사용해야 한다.",
            debt = Debt.FIVE_MINS,
        )

        override fun visitCallExpression(expression: KtCallExpression) {
            super.visitCallExpression(expression)

            val callee = expression.calleeExpression?.text ?: return
            if (callee in forbiddenAppBars) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        "$callee 사용 금지. NeveraAppBar, NeveraDisplayAppBar, " +
                            "NeveraLogoAppBar, NeveraSearchAppBar 중 적합한 것을 사용하라.",
                    )
                )
            }
        }
    }

**3-3. 마일스톤 3 테스트 작성**

`quality/detekt-rules/src/test/kotlin/com/anddd/nevera/quality/designsystem/rules/Material3AppBarRuleTest.kt`:

    package com.anddd.nevera.quality.designsystem.rules

    import io.gitlab.arturbosch.detekt.api.Config
    import io.gitlab.arturbosch.detekt.test.lint
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test

    class Material3AppBarRuleTest {

        private val rule = Material3AppBarRule(Config.empty)

        @Test
        fun `NeveraAppBar 사용은 위반 없음`() {
            val code = """
                Scaffold(
                    topBar = { NeveraAppBar(title = "제목") }
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).isEmpty()
        }

        @Test
        fun `TopAppBar 사용은 위반`() {
            val code = """
                Scaffold(
                    topBar = { TopAppBar(title = { Text("제목") }) }
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }

        @Test
        fun `CenterAlignedTopAppBar 사용은 위반`() {
            val code = """
                Scaffold(
                    topBar = { CenterAlignedTopAppBar(title = { Text("제목") }) }
                ) {}
            """.trimIndent()
            assertThat(rule.lint(code)).hasSize(1)
        }
    }

**3-4. 마일스톤 3 검증**

    ./gradlew :quality:detekt-rules:test

모든 테스트 PASSED.

    ./gradlew :feature:main:detekt

BUILD SUCCESSFUL이면 현재 코드에 위반 없음.


### 마일스톤 4: CI 통합

이 마일스톤이 끝나면 PR을 올릴 때마다 GitHub Actions CI가 Detekt를 실행하고, MVI 패턴 위반 또는 디자인 시스템 위반이 있으면 빌드가 실패한다.

**4-1. ci.yml에 Detekt 스텝 추가**

`.github/workflows/ci.yml`의 `steps` 섹션에 기존 Lint 스텝 뒤에 다음을 추가한다.

    - name: Run Detekt (패턴 규칙 검사)
      id: detekt
      run: ./gradlew detekt --no-daemon
      continue-on-error: true
      env:
        GOOGLE_WEB_CLIENT_ID: ${{ secrets.GOOGLE_WEB_CLIENT_ID }}

    - name: Upload Detekt Result
      uses: actions/upload-artifact@v5
      if: always()
      with:
        name: detekt-result
        path: '**/build/reports/detekt'

기존 `Check results` 스텝의 실패 조건에 Detekt를 추가한다. 기존:

    if [ "${{ steps.unit-test.outcome }}" = "failure" ] || \
       [ "${{ steps.lint.outcome }}" = "failure" ]; then

수정 후:

    if [ "${{ steps.unit-test.outcome }}" = "failure" ] || \
       [ "${{ steps.lint.outcome }}" = "failure" ] || \
       [ "${{ steps.detekt.outcome }}" = "failure" ]; then

**4-2. 마일스톤 4 검증**

로컬에서 위반 코드를 의도적으로 작성해 검증한다. `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/HomeViewModel.kt`에서 `NeveraViewModel` 대신 `ViewModel()`을 상속하도록 임시 수정한 뒤 실행한다.

    ./gradlew :feature:main:detekt

기대 출력:

    > Task :feature:main:detekt FAILED
    .../HomeViewModel.kt:10:1: [NeveraViewModelInheritanceRule] HomeViewModel 은 NeveraViewModel을 상속해야 한다.
    BUILD FAILED

위반이 탐지되면 수정을 되돌리고 다시 실행해 BUILD SUCCESSFUL을 확인한다. PR을 develop 브랜치로 올리면 GitHub Actions 탭에서 `Run Detekt (패턴 규칙 검사)` 스텝이 실행되는 것을 확인할 수 있다.


## Concrete Steps


모든 명령은 프로젝트 루트(`Nevera-Android/`)에서 실행한다.

1. `gradle/libs.versions.toml` 수정 — Detekt 버전, 라이브러리 3개, 플러그인 항목 추가
2. `build-logic/build.gradle.kts` 수정 — `detekt.gradlePlugin` compileOnly 의존성 추가, `neveraQuality` 플러그인 등록
3. `build-logic/src/main/kotlin/NeveraQualityPlugin.kt` 생성 — 두 config 파일을 참조하도록 작성
4. `build-logic/src/main/kotlin/NeveraFeaturePlugin.kt` 수정 — `pluginManager.apply("nevera.quality")` 추가
5. `settings.gradle.kts` 수정 — `include(":quality:detekt-rules")` 추가
6. `quality/detekt-rules/build.gradle.kts` 생성
7. `config/detekt/detekt-mvi.yml` 생성 — MVI 룰셋 설정
8. `config/detekt/detekt-designsystem.yml` 생성 — 디자인 시스템 룰셋 설정
9. `META-INF/services/io.gitlab.arturbosch.detekt.api.RuleSetProvider` 생성 — 두 Provider 등록
10. `NeveraMviRuleSetProvider.kt` 생성 (`mvi/` 패키지)
11. MVI 룰 4개 생성 (`mvi/rules/` 패키지)
12. MVI 룰 테스트 4개 생성 (`test/.../mvi/rules/` 패키지)
13. `NeveraDesignSystemRuleSetProvider.kt` 생성 (`designsystem/` 패키지)
14. 디자인 시스템 룰 1개 생성 (`designsystem/rules/` 패키지)
15. 디자인 시스템 룰 테스트 1개 생성 (`test/.../designsystem/rules/` 패키지)
16. `./gradlew :quality:detekt-rules:build` 실행 — 컴파일 오류 없음 확인
17. `./gradlew :quality:detekt-rules:test` 실행 — 전체 테스트 PASS 확인
18. `./gradlew :feature:main:detekt` 실행 — 기존 코드 위반 없음 확인
19. `./gradlew detekt` 실행 — 전체 프로젝트 위반 없음 확인
20. `.github/workflows/ci.yml` 수정 — Detekt 스텝 추가
21. 위반 코드 임시 삽입 → `./gradlew :feature:main:detekt` 실패 확인 → 되돌리기


## Validation and Acceptance


다음 명령과 결과로 완료를 판단한다.

`./gradlew :quality:detekt-rules:test` 실행 시:

    BUILD SUCCESSFUL in Xs
    X tests completed, 0 failed

`./gradlew detekt` 실행 시(기존 코드 기준):

    BUILD SUCCESSFUL in Xs

NeveraViewModel 상속 위반 코드 삽입 후 `./gradlew :feature:main:detekt` 실행 시:

    > Task :feature:main:detekt FAILED
    [NeveraViewModelInheritanceRule] HomeViewModel 은 NeveraViewModel을 상속해야 한다.
    BUILD FAILED in Xs

Material3 AppBar 위반 코드 삽입 후 `./gradlew :feature:main:detekt` 실행 시:

    > Task :feature:main:detekt FAILED
    [Material3AppBarRule] TopAppBar 사용 금지. NeveraAppBar, NeveraDisplayAppBar, ...
    BUILD FAILED in Xs

각 위반 코드를 되돌리면 다시 SUCCESSFUL이 나와야 한다.


## Idempotence and Recovery


모든 단계는 반복 실행해도 안전하다. Gradle 태스크는 멱등이며, 파일 생성은 이미 존재하면 덮어쓴다.

Detekt 룰 추가 후 기존 코드에 예상치 못한 위반이 다수 발생하면, 해당 config 파일(`detekt-mvi.yml` 또는 `detekt-designsystem.yml`)에서 해당 룰의 `active: false`로 임시 비활성화하고 Decision Log에 기록한 후 점진적으로 수정한다. 이 방식이 `baseline.xml`을 쓰는 것보다 더 명시적이다.


## Artifacts and Notes


Detekt가 성공적으로 설치된 후 `./gradlew :feature:main:detekt` 출력 예시:

    > Task :feature:main:detekt
    Detekt report: .../feature/main/build/reports/detekt/main.html
    BUILD SUCCESSFUL

위반 발견 시 출력 예시:

    > Task :feature:main:detekt FAILED
    .../HomeViewModel.kt:42:5: [ReduceOutsideApplyMutationRule]
        reduce { }는 applyMutation() 내부에서만 호출할 수 있다. 현재 위치: onRefreshClicked
    1 issue found.
    BUILD FAILED


## Interfaces and Dependencies


`:quality:detekt-rules` 모듈의 의존성:

- `io.gitlab.arturbosch.detekt:detekt-api:1.23.8` — `Rule`, `RuleSetProvider`, `CodeSmell`, `Issue`, `Entity` 등 커스텀 룰 작성에 필요한 모든 타입
- `io.gitlab.arturbosch.detekt:detekt-test:1.23.8` — `lint(code: String)` 테스트 유틸리티

RuleSetProvider가 구현하는 인터페이스:

    interface RuleSetProvider {
        val ruleSetId: String          // detekt.yml의 최상위 키와 일치해야 한다
        fun instance(config: Config): RuleSet
    }

각 Rule이 상속하는 기반 클래스와 핵심 오버라이드:

    abstract class Rule(config: Config) : DetektVisitor() {
        abstract val issue: Issue
        open fun visitClass(klass: KtClass) {}
        open fun visitNamedFunction(function: KtNamedFunction) {}
        open fun visitCallExpression(expression: KtCallExpression) {}
    }

Convention Plugin이 각 feature 모듈에 추가하는 의존성:

    dependencies {
        detektPlugins(project(":quality:detekt-rules"))
    }


---

변경 이력: 2026-07-07 — MVI 패턴 규칙과 디자인 시스템 규칙을 별도 `RuleSetProvider`(`NeveraMviRuleSetProvider`, `NeveraDesignSystemRuleSetProvider`)와 별도 config 파일(`detekt-mvi.yml`, `detekt-designsystem.yml`)로 분리했다. `ContentComposableParameterRule`은 MVI의 Screen/Content 분리 원칙에 해당하므로 MVI 룰셋에 포함하고, `Material3AppBarRule`은 디자인 시스템 룰셋으로 이동했다. CI 스텝은 단일 `./gradlew detekt`로 유지한다(양쪽 위반 모두 PR을 동일하게 차단해야 하므로 분리 실익 없음). 모든 패키지 경로와 테스트 패키지 경로를 `mvi/` 및 `designsystem/` 하위로 갱신했다.
