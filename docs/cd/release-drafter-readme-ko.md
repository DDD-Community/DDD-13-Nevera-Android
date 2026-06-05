# Release Drafter

PR이 머지될 때마다 다음 릴리즈 노트 초안을 자동으로 작성해주는 GitHub Action.

---

## github/workflows/release-drafter.yml - 사용법

`.github/workflows/release-drafter.yml` 워크플로우 파일을 아래와 같이 작성한다.

```yaml
name: Release Drafter

on:
  push:
    branches:
      - main
      - master

permissions:
  contents: write
  pull-requests: read

jobs:
  update_release_draft:
    runs-on: ubuntu-latest
    steps:
      - uses: release-drafter/release-drafter@v7
        with:
          config-name: release-drafter.yml  # 기본값, .github/release-drafter.yml 로드
```

---

## .github/release-drafter.yml - 설정

설정 파일의 기본 경로는 `.github/release-drafter.yml`이며, 액션이 GitHub API를 통해 직접 읽어간다. 별도로 `actions/checkout`을 실행할 필요가 없다.

### 최소 예시

```yaml
template: |
  ## What's Changed

  $CHANGES
```

PR이 머지될 때마다 Draft Release가 갱신되고, Publish 준비가 되면 수동으로 발행하면 된다.

### 카테고리 분류 + 버전 자동 계산 예시

```yaml
name-template: "v$RESOLVED_VERSION 🌈"
tag-template: "v$RESOLVED_VERSION"
categories:
  - title: "🚀 Features"
    semver-increment: minor
    when:
      labels:
        - "feature"
        - "enhancement"
  - title: "🐛 Bug Fixes"
    when:
      labels:
        - "fix"
        - "bugfix"
        - "bug"
  - title: "🧰 Maintenance"
    when:
      label: "chore"
  - type: "pre-exclude"
    when:
      label: "skip-changelog"
  - type: "version-resolver"
    semver-increment: "major"
    when:
      label: "major"
  - type: "version-resolver"
    semver-increment: "patch"
change-template: "- $TITLE @$AUTHOR (#$NUMBER)"
change-title-escapes: '\<*_&'
template: |
  ## Changes

  $CHANGES
```

---

## 설정 옵션 전체 목록

| 키 | 필수 여부 | 설명 |
|---|---|---|
| `template` | 필수 | Draft Release body 전체 템플릿. 템플릿 변수 사용 가능 |
| `header` | 선택 | `template` 앞에 붙는 고정 문구 |
| `footer` | 선택 | `template` 뒤에 붙는 고정 문구 |
| `category-template` | 선택 | 각 카테고리 제목 포맷. 기본값: `"## $TITLE"` |
| `name-template` | 선택 | Draft Release 이름 포맷. 예: `"v$NEXT_PATCH_VERSION"` |
| `tag-template` | 선택 | Draft Release 태그 포맷. 예: `"v$NEXT_PATCH_VERSION"` |
| `tag-prefix` | 선택 | 릴리즈 태그 필터링에 사용할 접두사. 버전 파싱 전 제거됨. 기본값: `""` |
| `version-template` | 선택 | 다음 버전 계산 포맷. 기본값: `"$MAJOR.$MINOR.$PATCH$PRERELEASE"` |
| `change-template` | 선택 | PR 한 줄 포맷. 기본값: `"* $TITLE (#$NUMBER) @$AUTHOR"` |
| `change-title-escapes` | 선택 | `$TITLE`에서 마크다운 특수문자로 해석되지 않도록 이스케이프할 문자 목록. 기본값: `""` |
| `no-changes-template` | 선택 | 포함할 변경사항이 없을 때 표시할 문구. 기본값: `"* No changes"` |
| `categories` | 선택 | 변경사항 필터링·그룹핑·버전 계산 규칙 정의 |
| `exclude-contributors` | 선택 | `$CONTRIBUTORS` 변수에서 제외할 사용자명 목록 |
| `no-contributors-template` | 선택 | 기여자가 없을 때 `$CONTRIBUTORS` 대체 문구. 기본값: `"No contributors"` |
| `replacers` | 선택 | 생성된 changelog body에서 정규식 검색·치환 |
| `sort-by` | 선택 | changelog 정렬 기준. `merged_at` 또는 `title`. 기본값: `merged_at` |
| `sort-direction` | 선택 | changelog 정렬 방향. `ascending` 또는 `descending`. 기본값: `descending` |
| `prerelease` | 선택 | 프리릴리즈 Draft 여부. 기본값: `false` |
| `prerelease-identifier` | 선택 | 프리릴리즈 식별자 (alpha, beta, rc 등). 지정 시 `prerelease: true` 자동 활성화 |
| `include-pre-releases` | 선택 | 이전 릴리즈 탐색 시 프리릴리즈 포함 여부. 기본값: `false` |
| `latest` | 선택 | 릴리즈를 latest로 표시할지 여부. `true`, `false`, `legacy`. 기본값: `true` |
| `commitish` | 선택 | 릴리즈 대상 브랜치 또는 커밋. 기본값: 워크플로우 실행 ref |
| `filter-by-range` | 선택 | semver 범위로 릴리즈 필터링. 기본값: `"*"` |
| `filter-by-commitish` | 선택 | `commitish`와 일치하는 릴리즈만 고려. 기본값: `false` |
| `pull-request-limit` | 선택 | PR 조회 API 호출 limit. 긴-lived 브랜치에서 사용. 기본값: `5` |
| `history-limit` | 선택 | 레포 탐색 시 pagination 창 크기. 기본값: `15` |

---

## 템플릿 변수

`template`, `header`, `footer`에서 사용 가능한 변수.

| 변수 | 설명 |
|---|---|
| `$CHANGES` | 머지된 PR 목록 (마크다운) |
| `$CONTRIBUTORS` | 릴리즈 기여자 목록 (콤마 구분) |
| `$PREVIOUS_TAG` | 직전 릴리즈 태그 |
| `$REPOSITORY` | 현재 레포지토리 이름 |
| `$OWNER` | 현재 레포지토리 소유자 |

---

## 버전 변수

`template`, `header`, `footer`, `name-template`, `tag-template`에서 사용 가능.

| 변수 | 설명 |
|---|---|
| `$NEXT_PATCH_VERSION` | 다음 patch 버전. 예: `v1.2.3` → `v1.2.4` |
| `$NEXT_MINOR_VERSION` | 다음 minor 버전. 예: `v1.2.3` → `v1.3.0` |
| `$NEXT_MAJOR_VERSION` | 다음 major 버전. 예: `v1.2.3` → `v2.0.0` |
| `$NEXT_PRERELEASE_VERSION` | 다음 프리릴리즈 버전. `prerelease-identifier` 의존. 예: `v1.2.3-beta.3` |
| `$RESOLVED_VERSION` | categories의 `semver-increment` 기반으로 계산된 다음 버전 |

각 `$NEXT_{MAJOR,MINOR,PATCH}_VERSION` 변수에는 구성 요소 헬퍼 변수도 제공된다.

| 변수 | 설명 |
|---|---|
| `$NEXT_MAJOR_VERSION_MAJOR` | `$NEXT_MAJOR_VERSION`의 major 부분 |
| `$NEXT_MINOR_VERSION_MINOR` | `$NEXT_MINOR_VERSION`의 minor 부분 |
| `$NEXT_PATCH_VERSION_PATCH` | `$NEXT_PATCH_VERSION`의 patch 부분 |
| `$NEXT_PRERELEASE_VERSION_PRERELEASE` | 프리릴리즈 세그먼트. 예: `'-beta.3'` |

---

## Version Resolver

`semver-increment`가 지정된 카테고리가 `$RESOLVED_VERSION` 계산에 기여한다.

버전 결정 흐름:
1. `pre-include` / `pre-exclude` 카테고리가 먼저 실행되어 후보 PR을 필터링
2. `type: changelog` 카테고리: 해당 카테고리에 배정된 PR만 버전 계산에 기여
3. `type: version-resolver` 카테고리: changelog 섹션을 렌더링하지 않고 버전만 계산에 기여
4. 매칭된 increment 중 가장 높은 값이 최종 버전으로 선택

```yaml
categories:
  - type: "version-resolver"
    semver-increment: "major"
    when:
      label: "major"
  - type: "version-resolver"
    semver-increment: "minor"
    when:
      label: "minor"
  - type: "version-resolver"
    semver-increment: "patch"
    when:
      label: "patch"
  - type: "version-resolver"
    semver-increment: "patch"  # when 없음 → 아무것도 매칭 안 될 때의 기본값
```

---

## 카테고리 설정

`categories`로 변경사항 분류 파이프라인 전체를 정의한다.

### 카테고리 타입

| type | 동작 |
|---|---|
| `changelog` (기본값) | 매칭된 PR을 릴리즈 노트에 그룹으로 렌더링 |
| `pre-include` | 이 카테고리에 매칭된 PR만 이후 처리에 포함 (화이트리스트) |
| `pre-exclude` | 이 카테고리에 매칭된 PR을 이후 처리에서 완전 제외 (블랙리스트) |
| `version-resolver` | 릴리즈 노트 렌더링 없이 버전 계산에만 기여 |

`pre-include`는 항상 `pre-exclude`보다 먼저 실행된다.

### 카테고리 키

| 키 | 적용 대상 | 설명 |
|---|---|---|
| `type` | 모든 카테고리 | 카테고리 동작 방식. 기본값: `changelog` |
| `title` | `changelog` | 릴리즈 노트에 표시될 섹션 제목. `changelog` 타입에서 필수 |
| `when` | 모든 카테고리 | 매칭 조건. 생략 시 모든 변경사항에 매칭 |
| `exclusive` | `changelog`, `version-resolver` | `true`이면 이미 매칭된 PR은 이후 같은 타입 카테고리에서 제외. 기본값: `false` |
| `collapse-after` | `changelog` | 지정 개수 초과 시 `<details>`로 접기. `0`: 항상 접기, `-1`: 비활성. 기본값: `-1` |
| `semver-increment` | `changelog`, `version-resolver` | 버전 증가 기여. `major`, `minor`, `patch`. 기본값: `patch` |

### when 조건 키

| 키 | 설명 |
|---|---|
| `label` | `labels`의 단일 항목 축약 |
| `labels` | PR 라벨과 비교할 라벨 목록 |
| `labels-mode` | 라벨 매칭 방식. 기본값: `any` |
| `path` | `paths`의 단일 항목 축약 |
| `paths` | PR 변경 파일과 비교할 glob 패턴 목록 |
| `paths-mode` | 경로 매칭 방식. 기본값: `any` |

**매칭 모드:**
- `any`: 하나 이상 일치 (기본값)
- `all`: 모두 일치
- `only`: 모든 변경값이 설정된 집합에 포함
- `exactly`: 변경값과 설정값이 동일한 집합

**복합 조건 예시** (라벨 OR 경로):

```yaml
categories:
  - title: "🐛 Bug Fixes"
    when:
      - labels:
          - "bug"
          - "fix"
      - labels:
          - "regression"
        paths:
          - "src/**"
```

`when`이 배열이면 하나의 조건만 만족해도 매칭된다. 조건 내부에서 `labels`와 `paths`는 AND로 결합된다.

---

## 변경사항 제외

`type: pre-exclude` 카테고리를 사용하는 것이 권장 방식이다.

```yaml
categories:
  - type: "pre-exclude"
    when:
      label: "skip-changelog"
```

`skip-changelog` 라벨이 붙은 PR은 Draft Release에서 완전히 제외된다.

---

## 변경사항 포함 (화이트리스트)

특정 라벨이 붙은 PR만 포함하고 싶을 때 `type: pre-include`를 사용한다.

```yaml
categories:
  - type: "pre-include"
    when:
      labels:
        - "app-foo"
```

`app-foo` 라벨이 붙은 PR만 Draft Release에 포함된다.

---

## change-template 변수

`change-template`에서 사용 가능한 변수.

| 변수 | 설명 |
|---|---|
| `$NUMBER` | PR 번호. 예: `42` |
| `$TITLE` | PR 제목. `change-title-escapes`에 지정된 문자는 자동 이스케이프 |
| `$AUTHOR` | PR 작성자 username |
| `$BODY` | PR 본문 |
| `$URL` | PR URL |
| `$BASE_REF_NAME` | 대상 브랜치 이름. 예: `master` |
| `$HEAD_REF_NAME` | 소스 브랜치 이름. 예: `my-bug-fix` |

---

## 기여자 제외

`$CONTRIBUTORS` 변수에서 특정 사용자를 제외할 수 있다.

```yaml
exclude-contributors:
  - "myusername"
```

---

## Replacers (내용 치환)

생성된 changelog body에서 정규식으로 텍스트를 검색·치환한다. 순서대로 적용된다.

```yaml
replacers:
  - search: '/CVE-(\d{4})-(\d+)/g'
    replace: "https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-$1-$2"
  - search: "myname"
    replace: "My Name"
  - search: "/- ([a-z])/g"
    replace: '- \u$1'  # 각 항목 첫 글자 대문자
```

---

## Autolabeler

PR이 열릴 때 파일 경로·브랜치명·제목·본문을 기준으로 라벨을 자동 부착할 수 있다.

```yaml
# .github/workflows/auto-label.yml
name: Auto Label

on:
  pull_request:
    types: [opened, reopened, synchronize]

permissions:
  contents: read

jobs:
  auto_label:
    permissions:
      pull-requests: write
    runs-on: ubuntu-latest
    steps:
      - uses: release-drafter/release-drafter/autolabeler@v7
```

```yaml
# .github/release-drafter.yml
autolabeler:
  - label: "chore"
    files:
      - "*.md"
    branch:
      - '/docs{0,1}\/.+/'
  - label: "bug"
    branch:
      - '/fix\/.+/'
    title:
      - "/fix/i"
  - label: "enhancement"
    branch:
      - '/feature\/.+/'
    body:
      - "/JIRA-[0-9]{1,4}/"
```

매처 종류: `files` (glob), `branch` (regex), `title` (regex), `body` (regex).
하나 이상의 매처가 조건을 만족하면 라벨이 붙는다.

---

## 프리릴리즈 워크플로우

```yaml
jobs:
  update_full_release_draft:
    runs-on: ubuntu-latest
    steps:
      - uses: release-drafter/release-drafter@v7
        with:
          prerelease: false  # 정식 릴리즈 Draft

  update_prerelease_draft:
    runs-on: ubuntu-latest
    steps:
      - uses: release-drafter/release-drafter@v7
        with:
          prerelease: true
          prerelease-identifier: "rc"  # v1.2.3-rc.1 형태
```

두 job이 병렬로 실행된다.
- `update_full_release_draft`: 마지막 stable release 이후 변경사항을 정식 Draft에 누적
- `update_prerelease_draft`: 마지막 프리릴리즈 이후 변경사항을 프리릴리즈 Draft에 누적

---

## Action Inputs (워크플로우에서 직접 지정)

설정 파일의 값을 워크플로우에서 override할 수 있다.

| Input | 설명 |
|---|---|
| `config-name` | 설정 파일 이름 override |
| `token` | GitHub API 접근 토큰. 기본값: `${{ github.token }}` |
| `dry-run` | `true`이면 실제 쓰기 작업 없이 로그만 출력. 기본값: `false` |
| `name` | 릴리즈 이름 override (`name-template` 무시) |
| `tag` | 릴리즈 태그 override (`tag-template` 무시) |
| `version` | 릴리즈 버전 override (버전 자동 계산 무시) |
| `publish` | `true`이면 Draft 없이 즉시 발행 |
| `prerelease` | 프리릴리즈 여부. 기본값: `false` |
| `prerelease-identifier` | 프리릴리즈 식별자 (alpha, beta, rc 등) |
| `include-pre-releases` | 이전 릴리즈 탐색 시 프리릴리즈 포함. 기본값: `false` |
| `latest` | latest 마킹 여부 |
| `commitish` | 릴리즈 대상 브랜치 |
| `header` | template body 앞에 추가할 문자열 |
| `footer` | template body 뒤에 추가할 문자열 |

---

## Action Outputs (이후 스텝에서 참조 가능)

| Output | 설명 |
|---|---|
| `id` | 생성·갱신된 릴리즈 ID |
| `name` | 릴리즈 이름 |
| `tag_name` | 릴리즈 태그 이름 |
| `body` | Draft 릴리즈 body. 파일에 포함할 때 활용 가능 |
| `html_url` | 릴리즈 페이지 URL |
| `upload_url` | 릴리즈 에셋 업로드 URL |
| `resolved_version` | Version Resolver가 계산한 버전. 예: `6.3.1` |
| `major_version` | resolved_version의 major. 예: `6` |
| `minor_version` | resolved_version의 minor. 예: `3` |
| `patch_version` | resolved_version의 patch. 예: `1` |
