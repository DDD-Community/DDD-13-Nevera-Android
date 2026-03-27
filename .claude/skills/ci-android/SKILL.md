---
name: ci-android
description: |
    Nevera Android CI 설정 가이드 (GitHub Actions).
    CI 구조, 설계 결정 근거, Gradle 지식, 미래 확장 방향에 대한 질문에 사용한다.
    ci.yml 수정 요청, CI 관련 개념 질문, 새 Job 추가 계획 시 참조한다.
    Responds to: "CI 수정", "GitHub Actions", "ci.yml", "assembleDebug", "Gradle 데몬",
    "unit test CI", "lint CI", "UI 테스트 추가", "CD 추가", "--no-daemon", "continue-on-error"
user-invocable: false
---

당신은 Nevera Android 프로젝트의 CI 설정 가이드다. 역할은 현재 CI 구조와 설계 결정 근거를 바탕으로
CI 관련 질문에 답하고, 수정 요청 시 일관된 원칙을 유지하도록 안내하는 것이다.

답변하거나 코드를 수정할 때는 항상 아래 파일을 기준으로 판단한다:
- `.github/workflows/ci.yml`

## 현재 CI 구조

**트리거 조건**
- `develop` 브랜치 push 및 PR
- `main` 브랜치는 의도적으로 미포함 (현재 전략)

**Job: `develop-build`**

| 스텝 | 역할 |
|---|---|
| Checkout code | 소스 코드 체크아웃 |
| Set up JDK 17 | temurin 배포판 JDK 설정 |
| Cache Gradle packages | `~/.gradle/caches`, `~/.gradle/wrapper` 캐싱 |
| Grant execute permission | `gradlew` 실행 권한 부여 |
| Run unit tests | `testDebugUnitTest --no-daemon` (continue-on-error) |
| Run Lint Debug | `lintDebug --no-daemon` (continue-on-error) |
| Upload Lint Debug Result | lint-results-debug.html artifact 업로드 |
| Upload Test Result | tests 디렉토리 artifact 업로드 |
| Check results | unit-test / lint outcome 확인 후 Job 성공/실패 결정 |

## 핵심 설계 결정

### 1. `assembleDebug` 미포함
Unit Test와 Lint는 자체적으로 컴파일을 포함한다. `assembleDebug`를 추가하면 DEX 변환과 APK 패키징이 발생하는데, 이 결과물을 현재 CI에서 아무도 사용하지 않아 낭비다.

`assembleDebug`가 필요한 경우:
- Instrumentation Test 실행 시 (에뮬레이터에 APK 설치 필요)
- Firebase App Distribution 등 CD 배포 시
- APK 크기/구조 분석 시

### 2. `--no-daemon` 적용
CI는 매번 새로운 Runner에서 실행되므로 Gradle 데몬을 재사용할 기회가 없다. 데몬이 메모리(~1GB)만 점유하고 이점은 없으므로 명시적으로 비활성화한다.

### 3. `continue-on-error` + `Check results` 패턴
Unit Test와 Lint는 서로 독립적인 검사다. 한쪽이 실패해도 나머지를 실행하고 artifact를 모두 생성해야 두 결과를 동시에 확인할 수 있다.

```yaml
- name: Run unit tests
  id: unit-test
  run: ./gradlew testDebugUnitTest --no-daemon
  continue-on-error: true          # 실패해도 다음 스텝 진행

- name: Run Lint Debug
  id: lint
  run: ./gradlew lintDebug --no-daemon
  continue-on-error: true

- name: Check results              # 최종 Job 성공/실패 결정
  if: always()
  run: |
    if [ "${{ steps.unit-test.outcome }}" = "failure" ] || \
       [ "${{ steps.lint.outcome }}" = "failure" ]; then
      exit 1
    fi
```

`continue-on-error`만 쓰면 실패해도 Job이 성공으로 표시된다. `Check results`가 실제 실패를 Job 수준으로 올려준다.

### 4. `main` 브랜치 트리거 미포함
현재 브랜치 전략상 의도적으로 제외. 추후 필요 시 `on.push.branches`와 `on.pull_request.branches`에 `main` 추가.

## 미래 확장 가이드

### UI 테스트 (Instrumentation Test) 추가 시
Unit Test + Lint Job과 **반드시 분리**한다. UI 테스트는 에뮬레이터 부팅이 필요해 10~30분이 소요되므로 같은 Job에 묶으면 모든 빌드가 느려진다.

```yaml
ui-test:
  runs-on: ubuntu-latest
  if: github.event_name == 'pull_request'  # PR 시에만 실행 권장
  steps:
    - uses: actions/checkout@v4
    - name: Build APKs
      run: |
        ./gradlew assembleDebug --no-daemon
        ./gradlew assembleDebugAndroidTest --no-daemon
    - name: Run UI Tests
      uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 34
        script: ./gradlew connectedDebugAndroidTest --no-daemon
```

### CD 추가 시
배포용 Job을 별도로 구성한다. Debug가 아닌 Release 빌드 사용.

| 배포 대상 | Gradle 명령 |
|---|---|
| Firebase App Distribution | `./gradlew assembleDebug` 또는 `assembleRelease` |
| Google Play Store | `./gradlew bundleRelease` (AAB 형식) |

## 핵심 규칙

- Unit Test + Lint만 있는 CI에는 `assembleDebug`를 추가하지 않는다
- CI의 모든 Gradle 명령에는 `--no-daemon`을 붙인다
- 독립적인 검사(test, lint)는 `continue-on-error` + `Check results` 패턴으로 구성한다
- UI 테스트는 Unit Test + Lint Job과 반드시 별도 Job으로 분리한다
- `continue-on-error`를 쓸 때는 반드시 `Check results` 스텝으로 마무리한다