# check-detekt

Claude Code가 응답을 마치려 할 때 변경된 `.kt` 파일이 있으면 `detekt`를 실행하고, 실패하면 종료를 차단해 에이전트가 스스로 위반을 수정하도록 만듭니다.

## 트리거

| 이벤트 | 조건 |
|--------|------|
| `Stop` | Claude가 응답을 종료하려는 시점. 변경·미추적 `.kt` 파일이 하나라도 있을 때만 실행 |

`git diff --name-only HEAD -- '*.kt'`와 `git ls-files --others --exclude-standard -- '*.kt'`가 모두 비어 있으면 즉시 통과합니다. `.kt` 변경이 없는 턴마다 Gradle을 돌리지 않기 위한 조건입니다.

## 동작

1. 변경된 `.kt` 파일 존재 여부 확인 — 없으면 통과
2. 재시도 카운터 확인 — 이미 3회면 안내 메시지를 출력하고, **카운터를 삭제한 뒤** 통과. 이때 위반이 해소됐는지, 사람이 개입했는지는 확인하지 않는다
3. **카운터를 먼저 증가시켜 기록**
4. `./gradlew detekt` 실행
5. 성공 시 카운터 삭제 후 통과
6. 실패 시 위반 목록을 요약해 `{"decision": "block", "reason": ...}` 출력 — Claude가 계속 수정

## 설계 결정

### 연속 재시도 3회 상한

`Stop` 훅에서 `block`을 반환하면 Claude는 작업을 이어갑니다. 수정에 실패하는 위반이 남아 있으면 검사 → 수정 시도 → 재검사가 무한히 반복될 수 있으므로, 최대 3회까지만 차단하고 이후에는 수동 확인을 안내합니다.

상한에 도달하면 카운터를 삭제하고 통과시켜, 최소한 턴이 끝나고 제어권이 사람에게 돌아가도록 합니다.

다만 스크립트는 **위반이 실제로 해소됐는지도, 사람이 손을 댔는지도 확인하지 않습니다.** 같은 실패 상태 그대로 다음 `Stop`이 오면 카운터가 0부터 다시 시작해 3회를 더 시도합니다. 즉 "세션당 총 3회"가 아니라 **"연속 3회마다 한 번은 반드시 멈춘다"** 에 가깝습니다. 사람이 개입할 틈을 주는 것이 목적이고, 재시도 총량을 제한하려는 것이 아닙니다.

카운터 파일은 `session_id`로 구분되므로 여러 세션이 서로의 횟수를 침범하지 않습니다.

### 카운터를 detekt 실행 *전에* 기록

훅에는 300초 타임아웃이 걸려 있습니다. detekt 실행 후에 카운터를 기록하면, 타임아웃으로 프로세스가 강제 종료됐을 때 카운터가 증가하지 않아 상한이 무력화됩니다. 그래서 실행 전에 먼저 기록하고, 성공한 경우에만 되돌려 삭제합니다.

```bash
RETRY_COUNT_NEXT=$((RETRY_COUNT + 1))
echo "$RETRY_COUNT_NEXT" > "$COUNTER_FILE"   # detekt 실행 전에 기록

DETEKT_OUTPUT=$(./gradlew detekt 2>&1)
DETEKT_EXIT=$?

if [[ "$DETEKT_EXIT" -eq 0 ]]; then
    rm -f "$COUNTER_FILE"                     # 성공 시에만 삭제
    exit 0
fi
```

### detekt 출력을 셸 보간 없이 전달

`reason` 필드는 JSON이어야 하므로 python으로 직렬화합니다. 이때 detekt 출력(파일 경로·위반 메시지)은 신뢰할 수 없는 텍스트이므로 셸 문자열 보간으로 python에 넘기지 않고, 임시 파일에 쓴 뒤 경로만 인자로 전달합니다. 커맨드 인젝션을 차단하기 위한 조치입니다.

## 상태 파일

`.claude/hooks/.state/detekt-retry-<session_id>`

세션별 재시도 횟수를 담습니다. detekt가 통과하면 삭제됩니다.

## 설정 위치

`.claude/settings.json`

```json
{
  "hooks": {
    "Stop": [
      {
        "matcher": "*",
        "hooks": [
          {
            "type": "command",
            "command": "bash ${CLAUDE_PROJECT_DIR}/.claude/hooks/check-detekt.sh",
            "timeout": 300
          }
        ]
      }
    ]
  }
}
```

## 스크립트 위치

`.claude/hooks/check-detekt.sh`

## 관련 문서

- [아키텍처 규칙의 강제](../../../README.md#아키텍처-규칙의-강제) — 이 훅이 실행하는 Detekt 커스텀 룰 9종
