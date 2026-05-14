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
