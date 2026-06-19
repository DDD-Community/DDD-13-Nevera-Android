# AI Agent (Claude Code) 가이드

이 디렉토리는 Nevera 프로젝트에서 [Claude Code](https://claude.ai/code)를 활용해 개발 생산성을 높이기 위한 **스킬(Skill)** 과 **훅(Hook)** 문서를 담고 있습니다.

---

## 스킬 (Skills)

스킬은 자연어 요청 또는 `/스킬명` 직접 호출로 트리거하는 자동화 단위입니다. 코드 생성·액션을 즉시 실행하는 **🛠️ 실행** 스킬과, 관련 질문 시 Claude Code가 자동으로 참조하는 **📖 참조** 스킬로 구분됩니다.

| 카테고리 | 스킬 | 설명 | 유형 |
|----------|------|------|------|
| **[개발 자동화](skills/dev-automation.md)** | [`/create-feature-module`](skills/dev-automation.md#create-feature-module) | Feature 모듈 MVI 보일러플레이트 자동 생성 | 🛠️ 실행 |
| | [`/implement-compose-preview`](skills/dev-automation.md#implement-compose-preview) | Composable에 `@Preview` 함수 자동 생성 | 🛠️ 실행 |
| | [`/recommend-commit-message`](skills/dev-automation.md#recommend-commit-message) | Staged 변경사항 분석 후 커밋 메시지 추천 | 🛠️ 실행 |
| | [`/sync-develop`](skills/dev-automation.md#sync-develop) | upstream develop 브랜치 동기화 & push | 🛠️ 실행 |
| **[PR / CI](skills/pr-ci.md)** | [`/create-pr`](skills/pr-ci.md#create-pr) | 브랜치 분석 → 코드 리뷰 → PR 템플릿 작성 → PR 생성 | 🛠️ 실행 |
| | [`/pr-auto-assign`](skills/pr-ci.md#pr-auto-assign) | PR Auto Assign 워크플로우 가이드 | 📖 참조 |
| | [`/ci-android`](skills/pr-ci.md#ci-android) | GitHub Actions CI 구조 및 설정 가이드 | 📖 참조 |
| **[디자인 시스템](skills/design-system.md)** | [`/design-system-color`](skills/design-system.md#design-system-color) | 시맨틱 컬러 토큰 선택 가이드 | 📖 참조 |
| | [`/design-system-typography`](skills/design-system.md#design-system-typography) | 타이포그래피 토큰 선택 가이드 | 📖 참조 |
| | [`/design-system-spacing`](skills/design-system.md#design-system-spacing) | 스페이싱 토큰 및 패딩/갭 가이드 | 📖 참조 |
| | [`/design-system-shape`](skills/design-system.md#design-system-shape) | Corner radius 토큰 선택 가이드 | 📖 참조 |

> 🛠️ **실행** — `/스킬명` 직접 호출 또는 자연어로 트리거하면 코드 생성·액션을 즉시 실행합니다.  
> 📖 **참조** — `/스킬명` 직접 호출 불가. 관련 질문·요청 시 Claude Code가 자동으로 참조해 답변에 반영합니다.

---

## 훅 (Hooks)

훅은 Claude Code의 특정 이벤트(툴 실행 전후 등)에 자동으로 반응하는 스크립트입니다. 사용자나 Claude의 별도 호출 없이 동작하며, 프로젝트 규칙을 코드 작성 흐름 안에서 자동으로 강제합니다.

| 훅 | 트리거 | 목적 |
|----|--------|------|
| [`check-appbar`](hooks/check-appbar.md) | `.kt` 파일 편집 직후 | `Scaffold`의 `topBar`에 Nevera AppBar 사용 여부 검증 |

---

## 사용 팁

- **자연어로 요청**해도 Claude Code가 자동으로 적합한 스킬을 선택합니다.
- **`/스킬명`** 으로 직접 호출하면 더 빠르게 실행됩니다.
- **디자인 시스템 스킬**은 `user-invocable: false`로 설정되어 있어 직접 호출보다 자연어 질문으로 트리거하는 것이 자연스럽습니다.
- **`/create-feature-module`** 과 **`/create-pr`** 은 사용자 확인 단계를 거치므로 실수로 코드가 생성되거나 PR이 열리는 일이 없습니다.
