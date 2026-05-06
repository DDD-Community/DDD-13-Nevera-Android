# build-logic

Nevera 프로젝트의 Gradle Convention Plugin 모음입니다.

## Plugin 목록

| Plugin ID | 역할 | 내부 조합 |
| --- | --- | --- |
| `nevera.test.unit` | JVM 단위 테스트 의존성 | — |
| `nevera.test.android` | Android 테스트 설정 + 의존성 | `nevera.test.unit` |
| `nevera.network` | Retrofit + OkHttp (Gson 기반 네트워크 공통화) | — |
| `nevera.firebase` | Firebase Messaging | — |
| `nevera.kotlin.jvm` | 순수 Kotlin JVM 모듈 기본 구성 | `nevera.test.unit` |
| `nevera.android.library` | Android library 기본 구성 | `nevera.test.android` |
| `nevera.android.compose` | Compose Android library 구성 | `nevera.android.library` |
| `nevera.android.hilt` | Hilt + KSP 구성 | — |
| `nevera.feature` | Feature 화면 모듈 공통 구성 | `nevera.android.compose`, `nevera.android.hilt` |
| `nevera.android.application` | App 모듈 공통 구성 | `nevera.android.hilt`, `nevera.test.android` |

## 빠른 선택표

| 만들려는 모듈 | 추천 Plugin |
| --- | --- |
| Android 의존성 없는 순수 Kotlin 모듈 | `nevera.kotlin.jvm` |
| Android library (Hilt 없음) | `nevera.android.library` |
| Android library + Hilt | `nevera.android.library` + `nevera.android.hilt` |
| Compose library + Hilt | `nevera.android.compose` + `nevera.android.hilt` |
| Feature 화면 모듈 | `nevera.feature` |
| App 진입 모듈 | `nevera.android.application` |

관심사 plugin(`nevera.network`, `nevera.firebase`)은 위 base plugin에 추가 조합합니다.

## 상세 문서

- [Plugin별 포함 내용](docs/plugin-catalog.md)
- [수정 시 주의사항 및 검증 가이드](docs/maintenance.md)
