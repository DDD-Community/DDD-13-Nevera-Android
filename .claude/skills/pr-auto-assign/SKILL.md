---
name: pr-auto-assign
description: |
    Nevera PR Auto Assign 워크플로우 가이드 (GitHub Actions).
    assignee/reviewer 자동 등록 구조, 설계 결정 근거, 팀원 추가 방법에 대한 질문에 사용한다.
    pr-auto-assign.yml 수정 요청, 팀원 추가, 조건 변경 시 참조한다.
    Responds to: "PR auto assign", "reviewer 자동 등록", "assignee 자동 등록",
    "pr-auto-assign.yml", "팀원 추가", "reviewer 추가", "github-script"
user-invocable: false
---

당신은 Nevera Android 프로젝트의 PR Auto Assign 워크플로우 가이드다. 역할은 현재 워크플로우 구조와
설계 결정 근거를 바탕으로 수정 요청 시 일관된 원칙을 유지하도록 안내하는 것이다.

답변하거나 코드를 수정할 때는 항상 아래 파일을 기준으로 판단한다:
- `.github/workflows/pr-auto-assign.yml`

## 현재 워크플로우 구조

**트리거 조건**
- `pull_request` 이벤트: `opened`, `reopened` 타입

**Job: `auto-assign`**

| 스텝 | 역할 |
|---|---|
| Assign author and reviewer | PR author → assignee 등록, 나머지 개발자 → reviewer 등록 |

**현재 등록된 개발자**
- `JuhyeokLee97`
- `Minsu-Lee`

**로직 흐름**
```
PR 오픈
  → author 추출 (context.payload.pull_request.user.login)
  → reviewers = developers에서 author 제외한 나머지 (filter)
  → author를 assignee로 등록 (issues API)
  → reviewers가 있으면 reviewer로 등록 (pulls API)
```

## 핵심 설계 결정

### 1. `filter` 사용

`filter`는 배열을 반환하므로 `developers` 배열에 사용자명만 추가하면 코드 변경 없이 모든 팀원이 reviewer로 자동 등록된다.

```js
const reviewers = developers.filter(dev => dev !== author)
reviewers: reviewers  // 이미 배열이므로 감쌀 필요 없음
```

### 2. `issues.addAssignees` vs `pulls.requestReviewers` API 분리

GitHub에서 PR은 내부적으로 issue로 관리된다. 따라서 두 API가 분리되어 있다.

| 역할 | API |
|---|---|
| Assignee 등록 | `github.rest.issues.addAssignees` (`issue_number` 사용) |
| Reviewer 등록 | `github.rest.pulls.requestReviewers` (`pull_number` 사용) |

### 3. `reviewers.length > 0` 조건 가드

author가 `developers` 목록에 없는 외부 기여자일 경우 `filter` 결과가 전체 배열이 된다.
빈 배열을 API에 전달하면 오류가 발생할 수 있으므로 조건 가드를 둔다.

## 팀원 추가/변경 방법

`developers` 배열에 GitHub 사용자명만 추가하면 된다. 다른 코드 변경은 불필요하다.

```js
// 팀원 추가 예시
const developers = ['JuhyeokLee97', 'Minsu-Lee', 'NewMember']
```

추가 후 동작:
- `JuhyeokLee97`가 PR 오픈 → reviewer: `Minsu-Lee`, `NewMember`
- `NewMember`가 PR 오픈 → reviewer: `JuhyeokLee97`, `Minsu-Lee`

## 미래 확장 가이드

### 특정 브랜치에서만 동작시키기

```yaml
on:
  pull_request:
    types: [opened, reopened]
    branches:
      - develop
      - main
```

### label 기반 조건부 reviewer 지정

```js
const label = context.payload.pull_request.labels.map(l => l.name)
const reviewers = label.includes('backend')
  ? ['BackendDev']
  : developers.filter(dev => dev !== author)
```

## 핵심 규칙

- 팀원 변경 시 `developers` 배열만 수정한다
- reviewer 등록은 항상 `reviewers.length > 0` 조건을 확인한다
- assignee는 `issues` API, reviewer는 `pulls` API를 사용한다
- reviewer 목록은 `filter`로 관리해 팀원 추가 시 코드 변경이 없도록 한다