# 개발 자동화 스킬

반복적인 보일러플레이트 생성, 커밋 메시지 작성, 브랜치 동기화 등 개발 흐름 안에서 자주 발생하는 작업을 자동화하는 스킬 모음입니다. `/스킬명` 직접 호출 또는 자연어 요청으로 트리거합니다.

## 목차

- [/create-feature-module](#create-feature-module) — Feature 모듈 MVI 보일러플레이트 자동 생성
- [/implement-compose-preview](#implement-compose-preview) — Composable `@Preview` 자동 생성
- [/recommend-commit-message](#recommend-commit-message) — 커밋 메시지 추천
- [/sync-develop](#sync-develop) — upstream develop 동기화

---

### create-feature-module

**Feature 모듈 MVI 보일러플레이트를 한 번에 자동 생성합니다.**

모듈명을 인자로 전달하면 `build.gradle.kts`, MVI 타입 파일(`Intent` / `UiState` / `Mutation` / `SideEffect`), `ViewModel`, `Screen`, `Content`, `Navigation` 등 12개 파일을 자동으로 생성하고, `settings.gradle.kts` 및 `app/build.gradle.kts`에 모듈을 등록합니다.

**트리거 예시**
```
/create-feature-module profile
/create-feature-module user-profile
feature 모듈 만들어줘 (모듈명을 함께 입력)
```

**동작 방식**
1. 인자 검증 — 모듈명이 없으면 중단
2. 중복 체크 — `feature/{name}` 디렉토리가 이미 존재하면 중단
3. 디렉토리 구조 생성 (`main`, `model`, `component`, `navigation`, `test`, `androidTest`)
4. 12개 파일 생성 (Orbit MVI 기반 `NeveraViewModel` 상속 구조)
5. `settings.gradle.kts`에 `include(":feature:{name}")` 등록
6. `app/build.gradle.kts`에 `implementation(project(":feature:{name}"))` 등록
7. 완료 후 커스터마이징 가이드 출력

**이름 변환 규칙**

| 입력 | 디렉토리/패키지 | 클래스명 접두사 | 확장 함수명 |
|------|----------------|----------------|-------------|
| `profile` | `profile` | `Profile` | `profile` |
| `user-profile` | `userprofile` | `UserProfile` | `userProfile` |
| `MyPage` | `mypage` | `MyPage` | `myPage` |

**사용 예**
```
/create-feature-module notification

# 생성 결과
feature/notification/
  build.gradle.kts
  src/main/.../model/NotificationIntent.kt
  src/main/.../model/NotificationUiState.kt
  src/main/.../model/NotificationSideEffect.kt
  src/main/.../model/NotificationMutation.kt
  src/main/.../NotificationViewModel.kt
  src/main/.../component/NotificationContent.kt
  src/main/.../NotificationScreen.kt
  src/main/.../navigation/NotificationNavigation.kt
  ... (총 12개 파일)
```

---

### implement-compose-preview

**Composable 함수에 `@Preview` 함수를 자동으로 생성해 파일 하단에 추가합니다.**

enum, sealed class, Boolean 파라미터를 분석하여 각 상태별로 별도의 Preview를 생성합니다. 새 Composable 작성 직후 Claude Code가 자율적으로 실행하기도 합니다.

**트리거 예시**
```
/implement-compose-preview HomeContent.kt
/implement-compose-preview NeveraButton
Preview 만들어줘
@Preview 함수 추가해줘
```

**동작 방식**
1. 대상 파일 또는 함수명 파악 (인자가 없으면 직전 편집 파일 자동 감지)
2. `@Composable` 함수의 파라미터 분석
   - `enum` → 각 entry마다 별도 Preview
   - `sealed class` → 각 하위 타입마다 별도 Preview
   - `Boolean` → `true`/`false` 두 Preview (enum과 의미 중복이면 생략)
   - `String` → 한국어 샘플 텍스트
   - `List/Map` → 1~3개 더미 아이템
   - 람다 파라미터 → `{}`
3. `NeveraTheme {}` 로 감싸고 `showBackground = true`, `widthDp = 360` 고정
4. 파일 가장 하단에 `private` 함수로 추가

**컨벤션**
```kotlin
@Preview(name = "NeveraButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraButtonPrimaryPreview() {
    NeveraTheme {
        NeveraButton(type = ButtonType.Primary, text = "확인", onClick = {})
    }
}
```

---

### recommend-commit-message

**Staged 변경사항을 분석하여 Conventional Commits 형식의 커밋 메시지 2~3개를 추천합니다. 커밋은 실행하지 않습니다.**

다국어를 지원하며, 멀티 모듈 구조를 인식해 변경된 모듈명을 scope로 자동 추론합니다.

**트리거 예시**
```
커밋 메시지 추천해줘
/recommend-commit-message
/recommend-commit-message ENG     # 영어로
/recommend-commit-message KOR     # 한국어로 (기본값)
```

**동작 방식**
1. `git diff --staged` 실행 — staged 변경사항 없으면 중단
2. `git diff` 로 unstaged 변경사항 유무 확인 (경고만, 중단 안 함)
3. 프로젝트 스택 감지 (Gradle 멀티 모듈 → 모듈명을 scope 후보로)
4. 변경 목적 분석 — 2개 이상 독립 목적이면 커밋 분리 권장
5. type, scope, subject 추론 후 2~3개 후보 출력

**지원 언어**: `KOR`(기본) · `ENG` · `JPN` · `CHN` · `ESP` · `POR` · `FRA` · `DEU`

**출력 예시**
```
### Candidate 1 ✅ (Recommended)
feat(notification): 알림 목록 화면 추가

### Candidate 2
feat(notification): 미읽은 알림 목록 조회 기능 구현
```

---

### sync-develop

**upstream(팀 원본 저장소)의 `develop` / `main` 브랜치를 로컬에 merge하고 origin(개인 fork)에 자동 push합니다.**

미커밋 변경사항이 있으면 자동으로 stash했다가 완료 후 복원합니다.

**트리거 예시**
```
/sync-develop
upstream sync 해줘
브랜치 최신화해줘
develop 동기화
```

**동작 방식**
1. 현재 브랜치 저장, 미커밋 변경사항 stash
2. `upstream` remote 감지 (없으면 `origin` fallback)
3. `git fetch $REMOTE`
4. `develop` 브랜치: `merge --ff-only` → 성공 시 `git push origin develop`
5. `main` 브랜치: `merge --ff-only` → 성공 시 `git push origin main`
6. 원래 브랜치 복귀 + stash pop

**예외 처리**

| 상황 | 동작 |
|------|------|
| 미커밋 변경사항 | 자동 stash → 완료 후 자동 복원 |
| merge 충돌 | abort 후 원래 브랜치로 복귀, 충돌 파일 목록 출력 |
| `upstream` 없음 | `origin`으로 fallback, 사용자에게 알림 |
| 이미 최신 상태 | 정상 진행 ("Already up to date") |
