# Navigation 모듈 구조 개선안

## 0. 이 문서의 범위

[navigation-architecture-review.md](docs/navigation-architecture-review.md)와 짝을 이루는 문서입니다. 역할을 나눕니다.

| 문서 | 질문 | 다루는 것 |
|---|---|---|
| navigation-**architecture-review** | 무엇이 고장났는가 | P0 버그, 백스택 정책, 딥링크, 테스트 |
| navigation-**module-architecture** (이 문서) | 경계를 어디에 그을 것인가 | 모듈 구조, 결합도, 의존 방향, 통일성 |

이 문서는 버그를 다루지 않습니다. 대신 **"왜 그런 버그가 반복해서 생기는 구조인가"** 를 모듈 수준에서 봅니다.

작성 기준 커밋: `818cdd86`

> **선행 전제**: 이 문서의 제안은 리뷰 문서 §3.4(백스택 정책 명명)와 §3.3(그래프 계약 통일)이 끝난 뒤를 가정합니다. 그 둘이 이 문서가 옮기려는 "내용물"이기 때문입니다.

---

## 1. 지금 결합도는 어느 정도인가

측정치부터 놓습니다.

| 항목 | 값 |
|---|---|
| `:app` 프로덕션 코드 | 477줄 |
| `:app`이 의존하는 feature 모듈 | 7개 (전부) |
| `:app` → feature 심볼 참조 | 24회 |
| `NeveraNavHost.kt` 한 파일의 feature import | 16개 |
| feature 간 직접 의존 | 1건 (`feature:main` → `feature:notification`, **미사용**) |

`:app`은 477줄짜리 얇은 모듈인데, 그 안에서 7개 feature 전부를 이름으로 알고 있습니다. 실질적인 결과는 하나입니다.

> **화면을 하나 추가하면 반드시 `:app`을 편집해야 합니다.**

이 자체가 잘못은 아닙니다. Now in Android도 `:app`이 그래프를 조립합니다. 문제는 `:app`이 **조립만** 하는 게 아니라 **정책까지** 들고 있다는 점이고, 그건 리뷰 문서에서 다뤘습니다.

이 문서가 보려는 건 다른 축입니다. **feature끼리 가로로 이어져야 할 때 갈 곳이 없다**는 문제입니다.

---

## 2. 핵심 진단 — 수평 의존을 표현할 방법이 없다

현재 모듈 규칙은 사실상 이렇습니다.

```
app ──────► feature:*  (수직, 허용)
feature:* ─► core:*, domain  (수직, 허용)
feature:* ─► feature:*  (수평, 금지... 인데 명시된 적 없음)
```

수평 의존이 금지라는 규칙은 어디에도 적혀 있지 않고, 대신 금지된 것처럼 취급됩니다. 그런데 **feature끼리 연결되어야 하는 실제 요구는 계속 발생합니다.** 갈 곳이 없으니 우회로가 생겼습니다. 네 가지가 관측됩니다.

### 우회로 1 — 직접 의존을 시도한 흔적

```kotlin
// feature/main/build.gradle.kts:12
implementation(project(":feature:notification"))
```

`feature/main` 소스에는 이 패키지 import가 **하나도 없습니다.** 누군가 홈에서 알림으로 가는 길을 만들려다 방식을 바꾸고 선언만 남긴 것으로 보입니다. 규칙이 없으면 다음 사람은 이 선언을 보고 "해도 되는 거구나"라고 읽습니다.

### 우회로 2 — 도메인 계층으로 밀수

```kotlin
// domain/usecase/ingredient/IngredientFocusEventBus.kt
@Singleton
class IngredientFocusEventBus @Inject constructor() {
    private val focusChannel = Channel<Long>(Channel.BUFFERED)
    val focusRequests: Flow<Long> = focusChannel.receiveAsFlow()
}
```

`:app`이 보내고 `feature:fridge`가 받습니다. 두 모듈은 서로를 모르지만 `domain`을 경유해 연결됩니다. **`domain`이 "냉장고 화면"과 "스크롤 포커스"를 알게 된 대가로 수평 통신을 얻었습니다.**

이건 실수가 아니라 **구조적 압력의 결과**입니다. `:app`과 `feature:fridge` 사이에 UI 이벤트를 보낼 합법적 경로가 없으니, 둘 다 의존하는 유일한 공통 모듈로 밀어 넣은 것입니다.

### 우회로 3 — feature 타입이 app 시그니처를 관통

```kotlin
MainActivity ─► NeveraApp(googleAuthClient) ─► NeveraNavHost(googleAuthClient)
              ─► authNavGraph(googleAuthClient) ─► LoginScreen(googleAuthClient)
```

`GoogleAuthClient`는 `feature:auth` 내부 타입인데 `:app`의 함수 시그니처 3개를 오염시킵니다. `LoginScreen`은 이미 `hiltViewModel()`을 쓰므로 Hilt로 받을 수 있었지만, **"feature 내부 의존을 어디서 해결하는가"에 대한 규칙이 없어** 가장 쉬운 경로인 파라미터 관통이 선택됐습니다.

### 우회로 4 — `core:ui`가 적재소가 되고 있다

같은 압력이 UI에서도 관측됩니다. `core:ui`의 컴포넌트를 실제로 누가 쓰는지 세어 봤습니다.

| 컴포넌트 | 사용 feature |
|---|---|
| `NeveraAddIngredientFab` | fridge, main |
| `ReceiptCaptureModeBottomSheet` | fridge, main |
| `CategoryBottomSheet` | fridge, ingredient |
| `StorageLocationBottomSheet` | fridge, ingredient |
| `CostFieldRow` | fridge, ingredient |

전부 **정확히 두 개** feature가 공유합니다. 이건 "여러 화면이 쓰는 범용 컴포넌트"가 아니라 **"두 feature가 공유해야 하는데 서로 의존할 수 없어서 위로 올린 것"** 입니다. `EmptyContent`·`LoadingContent` 같은 진짜 범용 컴포넌트와 성격이 다릅니다.

### 종합

네 우회로의 원인은 하나입니다.

> **feature 간에 무언가를 공유해야 할 때, 유일한 방법이 "공통 조상 모듈로 올리기"뿐입니다.**

올릴 곳이 `domain`이면 계층이 깨지고, `core:ui`면 그 모듈이 부풀고, 못 올리면 `:app`을 관통합니다. **모듈을 더 쪼개는 게 목적이 아니라, 수평 관계를 표현할 어휘를 만드는 게 목적입니다.**

---

## 3. 통일성 — 같은 것을 다르게 부르고 있다

구조 이야기와 별개로, 규약이 갈라진 지점들이 있습니다.

### 3.1 navigation 패키지 위치가 4:4로 갈림

| 배치 | 모듈 |
|---|---|
| 모듈 루트 `navigation/` | auth, mypage, fridge, notification |
| 화면별 `<screen>/navigation/` | splash, main, sample, ingredient |

`ingredient`는 **둘 다** 씁니다 — `main/navigation/`에 그래프가 있고, `ocrcapture/`·`ocrerror/`·`photodetail/`·`registersuccess/` 각각에도 `navigation/`이 있습니다.

새 화면을 만드는 사람이 파일을 어디 둘지 판단할 근거가 없습니다. 실제로 파일 이름도 갈립니다 — `FridgeNavigation.kt`(모듈 단위) vs `OcrCaptureNavigation.kt`(화면 단위).

### 3.2 모듈 이름과 담긴 feature 이름이 어긋남

`feature:main`이 담고 있는 것은 `home`입니다. 패키지는 `com.anddd.nevera.feature.main.home`, Route는 `HomeRoute`, 화면은 `HomeScreen`. "main"이라는 단어는 그 안에 아무것도 대응하지 않습니다.

동시에 `splash/main/`, `notification/main/`, `sample/main/`, `fridge/main/`, `mypage/main/`, `ingredient/main/`에서 `main`은 **"모듈의 대표 화면"** 이라는 또 다른 뜻으로 쓰입니다. 같은 단어가 두 층위에서 다른 의미입니다.

### 3.3 그래프 유무에 따른 구조 차이가 암묵적

`navigation<GraphRoute>`로 중첩 그래프를 만드는 모듈(auth, mypage, ingredient)과 `composable<Route>` 하나만 노출하는 모듈(splash, home, fridge, notification)이 섞여 있는데, 언제 그래프로 승격해야 하는지 기준이 없습니다. `fridge`는 화면이 2개인데 그래프가 없고, `auth`는 2개인데 그래프가 있습니다.

---

## 4. 선택지

### 안 A — `:core:navigation` 단일 모듈

Route 계약·백스택 정책·`TopLevelDestination`을 한 모듈에 모으고, 모든 feature와 `:app`이 여기에 의존합니다.

### 안 B — feature별 `api` / `impl` 분리

`:feature:notification:api`(Route + navigate 확장)와 `:feature:notification:impl`(화면 + 그래프)로 쪼갭니다. 다른 feature는 `api`만 의존합니다.

### 안 C — DI 기반 그래프 레지스트리

각 feature가 Hilt multibinding으로 `NavGraphEntry`를 제공하고, `:app`은 주입받은 집합을 순회만 합니다. `:app`이 feature를 이름으로 알지 않게 됩니다.

### 비교

| 기준 | A: core:navigation | B: api/impl 분리 | C: DI 레지스트리 |
|---|---|---|---|
| 모듈 수 변화 | 20 → 21 | 20 → 28+ | 20 → 21 |
| `:app`의 feature 참조 | 24 → 약 8 (그래프 조립만) | 24 → 약 8 | 24 → 0 |
| feature 간 수평 통신 | ✅ `:core:navigation` 경유 | ✅ `api` 경유 | ⚠️ 여전히 없음 |
| 그래프의 컴파일 타임 검증 | ✅ 유지 | ✅ 유지 | ❌ **상실** |
| 신규 화면 1개 추가 비용 | 파일 2개 + `:app` 1줄 | 파일 2개 + `:app` 1줄 | 파일 2개 |
| 신규 feature 1개 추가 비용 | 모듈 1개 | **모듈 2개 + 빌드 파일 2개** | 모듈 1개 |
| 빌드 구성 시간 | 거의 변화 없음 | 증가 | 거의 변화 없음 |
| 되돌리기 | 쉬움 | 어려움 | 어려움 |

### 권고: 안 A

이유는 **규모**입니다.

목적지 20개, feature 모듈 8개, 리뷰어 3인 규모에서 안 B는 투자 회수가 안 됩니다. api/impl 분리가 값을 하는 지점은 (a) 모듈이 수십 개가 되어 컴파일 회피 이득이 커지거나, (b) 여러 팀이 같은 feature를 동시에 건드릴 때입니다. 지금은 둘 다 아닙니다. 반면 비용은 즉시 발생합니다 — feature 하나당 빌드 파일 2개, 화면 하나 만들 때 결정할 것 2배.

안 C는 **그래프의 컴파일 타임 검증을 포기하는 대가**가 너무 큽니다. 지금 `NeveraNavHost`는 잘못된 Route를 쓰면 컴파일이 깨집니다. 이 프로젝트가 타입 안전 라우팅을 일관되게 지켜온 이점을 스스로 버리는 선택입니다. `:app`의 feature 참조 24개를 0으로 만드는 건 매력적으로 들리지만, 그 24개는 실제로 문제를 일으킨 적이 없습니다 — 문제를 일으킨 건 §2의 우회로들입니다.

**안 A는 §2에서 진단한 문제를 정확히 겨냥하면서, 안 B로 가는 문을 닫지 않습니다.** `:core:navigation`이 실제로 커지고 특정 feature의 Route가 자주 바뀌어 전체 재컴파일이 아프기 시작하면, 그때 그 feature만 골라 `api` 모듈로 승격하면 됩니다. 지금 미리 여덟 개를 다 쪼갤 이유가 없습니다.

---

## 5. `:core:navigation` 설계

### 5.1 담는 것

```
core/navigation/
├─ NeveraRoute.kt              // 마커 인터페이스. 모든 Route의 상위 타입
├─ NavigationPolicy.kt         // replaceFlow / switchTopLevelTab / endSession / navigateSingleTop
├─ TopLevelDestination.kt      // 바텀 탭 정의 + 바텀바 노출 판정
└─ route/
   ├─ NotificationRoute.kt     // 2개 이상 feature가 목적지로 삼는 Route
   ├─ IngredientCaptureRoute.kt
   └─ AuthGraphRoute.kt
```

Route 중 **여기로 올리는 것은 "둘 이상의 feature가 목적지로 삼는 것"만** 입니다. 실측하면 셋입니다.

| Route | 어디서 이동하는가 |
|---|---|
| `NotificationRoute` | home, fridge, mypage (**3개**) |
| OCR 캡처 진입 | home, fridge (**2개**) |
| `AuthGraphRoute` | splash, mypage(로그아웃) (**2개**) |

`AppInfoRoute`·`EditFridgeIngredientRoute`·`PhotoDetailRoute`처럼 한 feature 안에서만 쓰이는 Route는 **그대로 각 feature에 둡니다.** 올리지 않습니다.

### 5.2 담지 않는 것 (중요)

`core:ui`가 적재소가 된 전철을 밟지 않으려면 배제 규칙을 먼저 못박아야 합니다.

- ❌ Composable — `:core:navigation`은 UI를 모릅니다. Compose 의존성 자체를 넣지 않습니다.
- ❌ ViewModel, UseCase, 도메인 모델
- ❌ 한 feature 안에서만 쓰이는 Route
- ❌ 화면 전이 콜백 타입 — 그건 각 feature의 `NavGraphBuilder` 확장 시그니처입니다

> 판단 기준: **"두 번째 feature가 이걸 필요로 하는가?"** 아니면 올리지 않습니다. 두 번째가 나타났을 때 올려도 늦지 않습니다.

### 5.3 의존 방향

```
                    ┌──────────┐
                    │   app    │  그래프 조립 + 그래프 밖 전이 콜백
                    └────┬─────┘
             ┌───────────┼───────────┐
             ▼           ▼           ▼
      ┌───────────┐ ┌─────────┐ ┌──────────┐
      │feature:main│ │f:fridge │ │f:notific.│   서로를 모름
      └─────┬─────┘ └────┬────┘ └─────┬────┘
            └────────────┼────────────┘
                         ▼
              ┌────────────────────┐
              │  core:navigation   │  Route 계약 + 정책 (UI 없음)
              └────────────────────┘
                         │
                         ▼
                  core:common, domain
```

핵심은 화살표가 **아래로만** 간다는 것입니다. `feature:main`이 알림으로 가고 싶으면 `feature:notification`이 아니라 `core:navigation`의 `NotificationRoute`를 씁니다. 두 feature는 여전히 서로를 모릅니다.

### 5.4 이 구조가 §2의 우회로를 어떻게 없애는가

| 우회로 | 해소 방식 |
|---|---|
| 1. `feature:main` → `feature:notification` | `core:navigation`의 `NotificationRoute`로 대체. 직접 의존 제거 |
| 2. `IngredientFocusEventBus` in `domain` | 포커스 대상을 Route 인자로 전달 (리뷰 문서 §3.5). `domain`에서 5개 타입 제거 |
| 3. `GoogleAuthClient` 4단계 관통 | Hilt로 `LoginViewModel`에 직접 주입. `:app` 시그니처에서 소거 |
| 4. `core:ui` 적재 | 직접적 해소는 아니지만, **같은 판단 기준**(§5.2)을 `core:ui`에도 적용하면 fridge+ingredient 전용 컴포넌트 3개의 소속을 재검토할 수 있음 |

4번은 이 문서 범위 밖이지만, **원인이 같다는 점**은 기록해 둘 가치가 있습니다. 수평 공유 어휘가 생기면 `core:ui`로 밀어올릴 압력도 함께 줄어듭니다.

---

## 6. feature 간 통신 원칙

Navigation은 수평 통신의 한 종류일 뿐입니다. 일반 규칙을 세워야 `IngredientFocusEventBus` 같은 게 다시 생기지 않습니다.

> **feature 모듈은 다른 feature 모듈을 의존하지 않는다.** 연결이 필요하면 아래 셋 중 하나로 표현한다.
>
> 1. **Route 인자** — 화면 A가 화면 B에 값을 넘길 때. 목적지가 명확한 대부분의 경우.
> 2. **`domain`의 진짜 도메인 상태** — 여러 화면이 같은 비즈니스 상태를 구독할 때. "미읽음 알림 존재"는 여기 해당(`ObserveUnreadNotificationUseCase`). **"어느 화면의 몇 번째 항목으로 스크롤"은 해당하지 않는다.**
> 3. **`:app`의 조립** — 그래프 밖으로 나가는 전이. 콜백으로 위임.

`IngredientFocusEventBus`가 어긋난 지점이 정확히 2번의 경계입니다. 도메인 상태의 모양(`Flow<Long>`)을 하고 있지만 실제로 표현하는 건 UI 명령입니다. **"이 값이 서버·DB에서 재도출 가능한가?"** 를 물으면 갈립니다 — 미읽음 여부는 재도출되고, 스크롤 대상은 안 됩니다.

---

## 7. 규칙을 빌드에 각인하기

이 프로젝트의 강점은 규칙을 문서가 아니라 **convention plugin과 detekt로 강제**한다는 점입니다. 같은 방식을 모듈 경계에도 적용합니다.

### 7.1 convention plugin이 의존성을 배급

```kotlin
// NeveraFeaturePlugin.kt
dependencies {
    "implementation"(project(":core:common"))
    "implementation"(project(":core:designsystem"))
    "implementation"(project(":core:ui"))
    "implementation"(project(":core:mvi"))
    "implementation"(project(":core:navigation"))                        // ← 추가
    "implementation"(project(":domain"))
    "implementation"(libs.findLibrary("navigation-compose").get())       // ← 리뷰 문서 §3.6
}
```

모든 feature가 같은 것을 같은 방식으로 받게 되어, 개별 `build.gradle.kts`에서 결정할 일이 줄어듭니다.

### 7.2 수평 의존을 빌드가 거부

규칙을 글로만 적으면 §2의 우회로 1이 반복됩니다. 구성 시점에 검사합니다.

```kotlin
// NeveraFeaturePlugin.kt
afterEvaluate {
    configurations.matching { it.name.endsWith("implementation", ignoreCase = true) }.configureEach {
        dependencies.withType<ProjectDependency>().configureEach {
            val target = path
            check(!target.startsWith(":feature:") || target == project.path) {
                """
                feature 모듈은 다른 feature 모듈에 의존할 수 없습니다: ${project.path} → $target
                공유가 필요하면 :core:navigation(Route) 또는 :domain(도메인 상태)을 경유하세요.
                근거: docs/navigation-module-architecture.md §6
                """.trimIndent()
            }
        }
    }
}
```

에러 메시지가 **대안과 근거 문서를 함께 알려주는 것**이 핵심입니다. 막기만 하면 다음 사람은 또 다른 우회로를 찾습니다.

> 이 검사를 켜기 전에 `feature:main`의 미사용 `:feature:notification` 선언을 제거해야 합니다. 안 그러면 즉시 빌드가 깨집니다.

### 7.3 detekt 룰

리뷰 문서 §3.8의 룰 3종에 더해, 모듈 경계용으로 하나를 추가할 수 있습니다.

| 룰 | 검사 |
|---|---|
| `RouteInSharedModuleRule` | 둘 이상 feature에서 참조되는 Route가 feature 모듈에 남아 있는지 — **다만 detekt는 파일 단위라 판정 불가**. Gradle 검사(7.2)나 코드 리뷰로 다룸 |

솔직하게 적자면 이건 detekt로 안 됩니다. 7.2의 빌드 검사가 실효적인 유일한 자동화 수단입니다.

---

## 8. 패키지·네이밍 통일 규약

§3의 갈라짐을 하나로 정합니다.

### 8.1 navigation 파일 위치

> **모듈 루트의 `navigation/` 패키지에 둔다. 화면별 `navigation/`을 만들지 않는다.**

```
feature/ingredient/src/main/kotlin/com/anddd/nevera/feature/ingredient/
├─ navigation/
│  └─ IngredientNavigation.kt      ← 그래프 + 모든 Route + navigate 확장
├─ main/          (화면)
├─ ocrcapture/    (화면)
├─ ocrerror/      (화면)
└─ ...
```

근거: Route는 **모듈의 공개 계약**이지 특정 화면의 내부 사항이 아닙니다. 한 파일에 모여 있으면 그 모듈이 밖으로 무엇을 노출하는지 한눈에 보입니다. 지금은 `ingredient`의 계약을 파악하려면 파일 5개를 열어야 합니다.

파일명은 `<모듈명>Navigation.kt` 하나로 통일합니다.

### 8.2 Route 가시성

> **`:app`이나 다른 feature가 참조하는 Route만 public. 나머지는 `internal`.**

`private`은 쓰지 않습니다(mypage가 현재 사용 중). 같은 파일 안에서만 쓰인다는 사실이 우연히 성립할 뿐이고, 파일을 나누는 순간 깨집니다.

### 8.3 모듈·패키지 이름

- `feature:main` → **`feature:home`으로 개명**. 모듈 이름이 담긴 feature와 일치해야 합니다.
- 단일 화면 모듈의 `main/` 서브패키지는 유지하되, 그 뜻을 "모듈의 대표 화면"으로 문서에 고정합니다. `feature:main`이 사라지면 `main`이라는 단어의 중의성도 함께 해소됩니다.

> 개명은 import 경로가 전부 바뀌는 큰 diff입니다. 다른 Navigation 작업과 **섞지 말고 단독 커밋**으로 처리해야 리뷰가 가능합니다.

### 8.4 그래프 승격 기준

> **화면이 2개 이상이고 그 화면들이 하나의 사용자 플로우를 이룰 때 `navigation<GraphRoute>`로 승격한다.**

- `auth`(로그인→가입), `ingredient`(촬영→인식→완료), `mypage`(마이페이지→설정들) → 그래프 ✅
- `fridge`(냉장고 목록 + 수정) → 수정 화면은 목록에서만 진입하는 종속 화면. 그래프로 승격할지 지금 결정해 두면 다음 화면 추가 때 논쟁이 없습니다.

---

## 9. 마이그레이션 경로

각 단계는 독립 머지 가능하고, 되돌릴 수 있습니다.

| 단계 | 작업 | 위험 | 선행 |
|---|---|---|---|
| **1** | `feature:main`의 미사용 `:feature:notification` 의존 제거 | 없음 | — |
| **2** | `:core:navigation` 모듈 생성 (빈 껍데기 + convention plugin 배선) | 없음 | — |
| **3** | 백스택 정책 함수를 `:app` → `:core:navigation`으로 이동 | 낮음 | 리뷰 문서 §3.4 |
| **4** | 공유 Route 3개(`NotificationRoute`, OCR 캡처, `AuthGraphRoute`)를 `:core:navigation`으로 이동 | 낮음 (기계적) | 2 |
| **5** | `TopLevelDestination` 이동 + 바텀바 판정 분리 | 낮음 | 4 |
| **6** | 7.2 수평 의존 빌드 검사 활성화 | 낮음 | 1 |
| **7** | `GoogleAuthClient` Hilt 주입 전환 | 중간 | — |
| **8** | `IngredientFocusEventBus` 제거 → Route 인자화 | 중간 | 리뷰 문서 §3.5 결정 |
| **9** | navigation 패키지 위치 통일 (§8.1) | 낮음 (기계적, diff 큼) | — |
| **10** | `feature:main` → `feature:home` 개명 | 낮음 (기계적, diff 매우 큼) | 단독 커밋 |

**1·2는 지금 바로 가능합니다.** 3~5가 이 문서의 본체이고, 6이 규칙을 고정합니다. 7~10은 언제든 독립적으로 할 수 있습니다.

---

## 10. 하지 않기로 한 것

명시하지 않으면 나중에 "왜 안 했지?"가 반복됩니다.

| 하지 않음 | 이유 | 재검토 조건 |
|---|---|---|
| feature별 `api`/`impl` 분리 (안 B) | 모듈 8개·목적지 20개 규모에서 비용 > 이득 | feature가 15개를 넘거나, 특정 feature의 Route 변경으로 인한 전체 재컴파일이 실제로 아파질 때 |
| DI 그래프 레지스트리 (안 C) | 그래프의 컴파일 타임 검증 상실이 이득보다 큼 | 동적 모듈(Play Feature Delivery) 도입 시 |
| 모든 Route를 `:core:navigation`으로 이동 | 그 모듈이 `core:ui`처럼 적재소가 됨 | — (영구히 하지 않음. §5.2 기준 유지) |
| `:app`의 feature 참조를 0으로 만들기 | 24개 참조는 문제를 일으킨 적 없음. 조립 지점이 하나라는 건 오히려 장점 | — |
| `core:ui`의 fridge+ingredient 전용 컴포넌트 재배치 | 이 문서 범위 밖. 원인은 같지만 별도 판단 필요 | Navigation 작업 완료 후 별도 과제로 |

---

## 11. 열린 질문

1. **`feature:main` → `feature:home` 개명을 할 것인가?** 기계적이지만 diff가 매우 큽니다. 이름의 중의성이 실제로 혼란을 일으키고 있는지, 아니면 익숙해져서 괜찮은지는 팀이 판단해야 합니다.

2. **`fridge`를 그래프로 승격할 것인가?** 화면 2개짜리 모듈의 처리 기준을 지금 정하면 다음 화면 추가 때 논쟁이 없습니다.

3. **`:core:navigation`을 순수 Kotlin(JVM) 모듈로 만들 수 있는가?** Compose를 배제하기로 했으므로(§5.2) 이론상 가능하고, 그러면 빌드가 빨라지고 UI 의존이 물리적으로 차단됩니다. 다만 `NavOptions`·`NavController` 등 `androidx.navigation` API가 Android 모듈을 요구하는지 확인이 필요합니다. **확인 전까지는 Android 라이브러리 모듈로 시작**하고, 가능하다면 나중에 낮추는 편이 안전합니다.

4. **공유 Route의 소유권은 누구인가?** `NotificationRoute`가 `:core:navigation`으로 가면 `feature:notification` 팀이 자기 Route를 자기 모듈에서 못 보게 됩니다. 규모상 지금은 문제가 아니지만, 리뷰어 지정 규칙(`.github/workflows/pr-auto-assign.yml`)과 어긋나지 않는지 한 번 볼 가치가 있습니다.

---

## 12. 요약

`:app`이 7개 feature를 전부 아는 것 자체는 문제가 아닙니다. 조립 지점이 하나인 건 오히려 장점입니다.

진짜 문제는 **feature끼리 가로로 이어질 어휘가 없다**는 것이고, 그 결과가 네 개의 우회로로 관측됩니다 — 미사용 직접 의존, `domain`으로 밀수한 이벤트 버스, 4단계를 관통하는 feature 타입, 두 feature 전용 컴포넌트가 쌓이는 `core:ui`.

처방은 모듈을 많이 쪼개는 게 아니라 **하나를 정확히 추가하는 것**입니다. `:core:navigation`이 Route 계약과 백스택 정책을 갖고, feature는 서로를 모른 채 그것만 참조합니다. 무엇을 올리고 무엇을 올리지 않을지의 기준(§5.2)을 먼저 못박아야 이 모듈이 `core:ui`의 전철을 밟지 않습니다.

그리고 규칙은 문서가 아니라 **빌드가 거부하는 형태**(§7.2)로 남겨야 합니다. 이 프로젝트가 MVI와 Screen/Content 경계에서 이미 증명한 방식입니다.
