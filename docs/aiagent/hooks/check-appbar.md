# check-appbar

`Scaffold`의 `topBar`에 Material3 기본 AppBar 대신 Nevera 디자인 시스템 AppBar가 사용되었는지 자동으로 검증합니다.

## 트리거

| 이벤트 | 조건 |
|--------|------|
| `PostToolUse` | `Edit` 또는 `Write` 도구로 `.kt` 파일을 편집한 직후 |

## 검증 로직

1. 편집된 파일이 `.kt`가 아니면 즉시 통과
2. 파일 내 `Scaffold(` 패턴이 없으면 통과
3. `topBar` 파라미터가 있을 때, 아래 허용 컴포넌트 중 하나가 사용되었는지 확인
4. 위반 시 `exit 2`로 Claude Code에 경고 전달 — Claude가 수정 방향을 안내

## 허용 컴포넌트

| 컴포넌트 | 용도 |
|----------|------|
| `NeveraAppBar` | 일반 상세·설정 화면. 중앙 제목 + 좌측 navigation + 우측 action |
| `NeveraDisplayAppBar` | 섹션 진입·타이틀 강조 화면. 좌측 큰 제목 + 우측 action |
| `NeveraLogoAppBar` | 메인·브랜드 화면. 좌측 로고 + 우측 action |
| `NeveraSearchAppBar` | 검색 화면. 검색 UI를 AppBar 내에 직접 배치 |

## 위반 예시 vs 올바른 예시

```kotlin
// ❌ 위반 — 훅이 경고를 발생시킨다
Scaffold(
    topBar = { TopAppBar(title = { Text("제목") }) }
) { ... }

// ✅ 통과
Scaffold(
    topBar = {
        NeveraAppBar(
            title = "제목",
            navigation = NeveraAppBarNavigation.Back(onClick = onBackClick),
        )
    }
) { ... }
```

## 설정 위치

`.claude/settings.json`

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "bash .claude/hooks/check-appbar.sh"
          }
        ]
      }
    ]
  }
}
```

## 스크립트 위치

`.claude/hooks/check-appbar.sh`
