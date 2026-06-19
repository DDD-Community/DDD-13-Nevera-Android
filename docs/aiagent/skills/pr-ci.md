# PR / CI 스킬

PR 생성 자동화, PR 자동 배정 워크플로우, GitHub Actions CI 구조에 관한 스킬 모음입니다. `/create-pr`은 직접 호출해 액션을 실행하며, `/pr-auto-assign`·`/ci-android`는 관련 질문·수정 요청 시 Claude Code가 자동으로 참조합니다.

## 목차

- [/create-pr](#create-pr) — PR 작성 및 생성 자동화
- [/pr-auto-assign](#pr-auto-assign) — PR Auto Assign 워크플로우 가이드
- [/ci-android](#ci-android) — GitHub Actions CI 설정 가이드

---

### create-pr

**현재 브랜치와 develop의 차이를 분석하고, 자체 코드 리뷰 → PR 템플릿 작성 → 사용자 확인 → PR 생성의 순서로 진행합니다.**

크리티컬 문제가 발견되면 PR 생성을 자동으로 중단하고 수정 방향을 제시합니다.

**트리거 예시**
```
/create-pr
PR 만들어줘
pull request 생성해줘
/create-pr 알림 기능 추가 작업입니다    # 작업 배경 전달
```

**동작 방식**
1. 현재 브랜치 확인 (develop/main이면 중단)
2. upstream/origin fetch 후 BASE 결정
3. `git log BASE..HEAD` — 커밋 없으면 중단
4. `git diff BASE...HEAD` — 전체 diff 파악
5. `./gradlew compileDebugKotlin` — 빌드 실패 시 중단
6. 자체 코드 리뷰 (크리티컬 기준 검토)
7. **크리티컬 발견** → 문제 목록 출력 후 중단
8. **통과** → PR 템플릿 초안 작성 + 제목·라벨 추론 → 사용자 확인
9. 승인 시 `gh pr create` 실행

**크리티컬 체크 항목**: 보안 키 하드코딩, 강제 언래핑(`!!`) 남용, 메모리 누수, 아키텍처 위반, 디자인 시스템 미준수, 빌드 파괴 등

**PR 제목 원칙**: 사용자 관점의 한국어, 내부 용어(`ViewModel`, `UiState` 등) 사용 금지, 타입 prefix 없음

---

### pr-auto-assign

**PR이 열릴 때 author를 assignee로, 나머지 팀원을 reviewer로 자동 등록하는 GitHub Actions 워크플로우 가이드입니다.**

워크플로우 수정이나 팀원 추가 요청 시 이 스킬이 자동으로 참조됩니다.

**트리거 예시**
```
팀원 추가해줘
reviewer 자동 등록 어떻게 돼?
pr-auto-assign.yml 수정해줘
```

**핵심 구조**
- 파일: `.github/workflows/pr-auto-assign.yml`
- 트리거: `pull_request_target` (`opened`, `reopened`)
- 인증: `secrets.AUTO_ASSIGN_PAT` (fork PR에서도 동작하도록 PAT 사용)

**팀원 추가 방법**

```js
// pr-auto-assign.yml 내 github-script 블록
const developers = ['Jooman-Lee', 'Minsu-Lee', 'NewMember']  // 이것만 수정
```

---

### ci-android

**GitHub Actions CI 구조와 설계 결정 근거를 안내합니다.** CI 수정 요청이나 새 Job 추가 계획 시 이 스킬이 자동으로 참조됩니다.

**트리거 예시**
```
CI에 unit test 추가하고 싶어
assembleDebug는 왜 없어?
UI 테스트 Job 추가하려면?
--no-daemon은 왜 쓰는 거야?
```

**현재 CI 구조** (`.github/workflows/ci.yml`)
- 트리거: `develop` 브랜치 push 및 PR
- Job: `develop-build`
  - JDK 17 세팅 + Gradle 캐시
  - `testDebugUnitTest --no-daemon` (continue-on-error)
  - `lintDebug --no-daemon` (continue-on-error)
  - 결과 artifact 업로드
  - `Check results` 스텝으로 최종 성공/실패 결정

**핵심 설계 원칙**
- `assembleDebug` 미포함 — Unit Test/Lint가 자체 컴파일 포함, APK가 필요 없으면 낭비
- `--no-daemon` — CI Runner는 매번 새로 시작되므로 데몬 재사용 불가, 메모리만 낭비
- `continue-on-error` + `Check results` — test/lint를 독립 실행하여 두 결과 동시 확인
