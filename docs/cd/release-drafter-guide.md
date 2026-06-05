# Nevera Release Drafter 구현 가이드

PR이 develop에 머지될 때마다 Draft Release를 자동으로 생성·갱신하는 Release Drafter 구현을 설명한다.

---

## 파일 구성

| 파일 | 역할 |
|---|---|
| `.github/workflows/release-drafter.yml` | 워크플로우 — 언제 실행할지 |
| `.github/release-drafter.yml` | 설정 — 릴리즈 노트를 어떻게 만들지 |

---

## 워크플로우 `.github/workflows/release-drafter.yml`

```yaml
name: Release Drafter

on:
  push:
    branches:
      - develop

jobs:
  update_release_draft:
    runs-on: ubuntu-latest
    permissions:
      contents: write
      pull-requests: read
    steps:
      - uses: release-drafter/release-drafter@v6
        with:
          config-name: release-drafter.yml
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### 트리거

`develop` 브랜치에 push가 발생할 때마다 실행된다. PR이 develop으로 머지되는 것이 push를 발생시키므로, **PR 머지 = Draft Release 갱신**이다.

### 권한

| 권한 | 이유 |
|---|---|
| `contents: write` | Draft Release 생성·수정 |
| `pull-requests: read` | 머지된 PR 목록 및 라벨 조회 |

### 실행 방식

`release-drafter/release-drafter@v6` 액션 하나만 실행한다. 이 액션은 GitHub API만으로 동작하기 때문에 `actions/checkout`이 필요 없다. `config-name: release-drafter.yml`은 기본값이지만 명시적으로 지정해두었다.

---

## 설정 `.github/release-drafter.yml`

### 버전 및 태그 이름

```yaml
name-template: 'v$RESOLVED_VERSION'
tag-template: 'v$RESOLVED_VERSION'
```

Draft Release의 이름과 태그가 `v1.2.3` 형식으로 자동 설정된다. `$RESOLVED_VERSION`은 머지된 PR의 라벨을 기준으로 아래 categories 규칙에 따라 계산된다.

---

### 카테고리

```yaml
categories:
  - title: '✨ 새 기능'
    semver-increment: minor
    when:
      labels: [feat, feature]

  - title: '🐛 버그 수정'
    semver-increment: patch
    when:
      labels: [fix, bug]

  - title: '🎨 디자인 변경'
    semver-increment: patch
    when:
      labels: [design, ui]

  - title: '⚡️ 성능 개선'
    semver-increment: patch
    when:
      labels: [perf, performance]

  - title: '🔧 내부 작업'
    when:
      labels: [chore, refactor, ci, test, docs]

  - type: pre-exclude
    title: '🚫 제외'
    when:
      labels: [skip-changelog]

  - type: version-resolver
    title: '💥 주요 변경'
    semver-increment: major
    when:
      labels: [breaking-change]

  - type: version-resolver
    title: '기본'
    semver-increment: patch
```

#### 렌더링되는 카테고리 (`type: changelog`)

`type`을 명시하지 않으면 기본값 `changelog`로, 릴리즈 노트에 섹션으로 표시된다.

| 카테고리 | 라벨 | 버전 증가 |
|---|---|---|
| ✨ 새 기능 | `feat`, `feature` | minor (`v0.1.0` → `v0.2.0`) |
| 🐛 버그 수정 | `fix`, `bug` | patch (`v0.1.0` → `v0.1.1`) |
| 🎨 디자인 변경 | `design`, `ui` | patch |
| ⚡️ 성능 개선 | `perf`, `performance` | patch |
| 🔧 내부 작업 | `chore`, `refactor`, `ci`, `test`, `docs` | patch (기본값) |

`🔧 내부 작업`은 `semver-increment`를 지정하지 않았으므로, 아래 `type: version-resolver`의 기본값 `patch`가 적용된다.

#### `type: pre-exclude` — 완전 제외

```yaml
- type: pre-exclude
  when:
    labels: [skip-changelog]
```

`skip-changelog` 라벨이 붙은 PR은 릴리즈 노트에 전혀 표시되지 않는다. 버전 계산에도 기여하지 않는다.

#### `type: version-resolver` — 버전 계산 전용

```yaml
# breaking-change 라벨 → major 증가
- type: version-resolver
  semver-increment: major
  when:
    labels: [breaking-change]

# 기본값 (when 없음) → 아무 라벨도 매칭 안 될 때 patch
- type: version-resolver
  semver-increment: patch
```

릴리즈 노트에는 렌더링되지 않고 버전 계산에만 기여한다. `breaking-change` 라벨이 붙은 PR이 있으면 major가 증가하고, 마지막 `when` 없는 항목이 기본 fallback이다.

#### 버전 결정 규칙 요약

여러 라벨이 섞여 있을 때는 가장 높은 increment가 선택된다.

| 머지된 PR 라벨 조합 | 최종 버전 증가 |
|---|---|
| `feat` + `fix` | minor (`feat`이 더 높음) |
| `fix` + `design` | patch |
| `breaking-change` + `feat` | major (`breaking-change`가 가장 높음) |
| `chore`만 | patch (기본값) |
| `skip-changelog`만 | patch (기본값, pre-exclude 후 fallback) |

---

### PR 포맷

```yaml
change-template: "- $TITLE @$AUTHOR (#$NUMBER)"
change-title-escapes: '\<*_&'
```

각 PR은 아래 형식으로 한 줄씩 표시된다.

```
- 로그인 화면 디자인 수정 @Jooman-Lee (#42)
```

`change-title-escapes`는 PR 제목에 `\`, `<`, `*`, `_`, `&` 같은 마크다운 특수문자가 포함되었을 때 자동으로 이스케이프해 깨짐을 방지한다.

---

### 릴리즈 노트 템플릿

```yaml
no-changes-template: '변경사항 없음'

template: |
  ## 변경사항

  $CHANGES
```

`$CHANGES`에 카테고리별로 분류된 PR 목록이 삽입된다. 포함할 PR이 하나도 없으면 `변경사항 없음`이 표시된다.

**생성 결과 예시:**

```
## 변경사항

### ✨ 새 기능
- 위시리스트 생성 기능 추가 @Jooman-Lee (#38)

### 🐛 버그 수정
- 로그인 크래시 수정 @Minsu-Lee (#41)

### 🔧 내부 작업
- CI Node.js 24 마이그레이션 @Jooman-Lee (#39)
```

---

## 전체 흐름

```
PR 라벨 부착 (feat, fix, design 등)
    ↓
develop에 PR 머지
    ↓
release-drafter.yml 워크플로우 실행
    ↓
직전 published release 이후 머지된 PR 라벨 스캔
    ↓
├── skip-changelog → pre-exclude (제외)
├── breaking-change → major 버전 증가
├── feat/feature → minor 버전 증가 + ✨ 새 기능 섹션
├── fix/bug → patch 버전 증가 + 🐛 버그 수정 섹션
├── design/ui → patch 버전 증가 + 🎨 디자인 변경 섹션
├── perf/performance → patch 버전 증가 + ⚡️ 성능 개선 섹션
└── chore/refactor/ci/test/docs → patch 버전 증가 + 🔧 내부 작업 섹션
    ↓
Draft Release body 갱신 (GitHub Releases 탭에서 확인 가능)
    ↓
cd-firebase.yml이 이 body를 읽어 Firebase 배포 릴리즈 노트로 사용
```

---

## PR 라벨 부착 규칙

Draft Release가 올바르게 생성되려면 PR에 아래 라벨 중 하나를 반드시 붙여야 한다.

| 라벨 | 용도 |
|---|---|
| `feat` / `feature` | 새 기능 |
| `fix` / `bug` | 버그 수정 |
| `design` / `ui` | UI/UX 변경 |
| `perf` / `performance` | 성능 개선 |
| `chore` | 유지보수, 설정 변경 |
| `refactor` | 코드 구조 개선 |
| `ci` | CI/CD 파이프라인 변경 |
| `test` | 테스트 추가·수정 |
| `docs` | 문서 변경 |
| `breaking-change` | 하위 호환 불가 변경 |
| `skip-changelog` | 릴리즈 노트 제외 |

라벨이 없으면 어느 카테고리에도 속하지 않아 `변경사항 없음`으로 표시될 수 있다.
