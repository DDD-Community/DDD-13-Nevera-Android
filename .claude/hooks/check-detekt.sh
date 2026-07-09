#!/bin/bash
# Stop hook: 변경된 .kt 파일이 있으면 detekt를 돌려 실패 시 Claude가 계속 수정하도록 block

set -uo pipefail

INPUT=$(cat)
SESSION_ID=$(echo "$INPUT" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(data.get('session_id', 'unknown'))
" 2>/dev/null || echo "unknown")

PROJECT_DIR="${CLAUDE_PROJECT_DIR:-$(pwd)}"
cd "$PROJECT_DIR" || exit 0

# 변경된 .kt 파일 없으면 패스 (매 턴마다 돌리지 않음)
CHANGED_KT=$(git diff --name-only HEAD -- '*.kt' 2>/dev/null)
UNTRACKED_KT=$(git ls-files --others --exclude-standard -- '*.kt' 2>/dev/null)
if [[ -z "$CHANGED_KT" && -z "$UNTRACKED_KT" ]]; then
    exit 0
fi

# 세션별 재시도 카운터 (무한루프 방지, 최대 3회)
STATE_DIR="$PROJECT_DIR/.claude/hooks/.state"
mkdir -p "$STATE_DIR"
COUNTER_FILE="$STATE_DIR/detekt-retry-$SESSION_ID"
MAX_RETRIES=3

RETRY_COUNT=0
if [[ -f "$COUNTER_FILE" ]]; then
    RAW_COUNT=$(cat "$COUNTER_FILE")
    [[ "$RAW_COUNT" =~ ^[0-9]+$ ]] && RETRY_COUNT="$RAW_COUNT"
fi

if [[ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]]; then
    rm -f "$COUNTER_FILE"
    echo "[detekt] ${MAX_RETRIES}회 자동 수정 시도 후에도 실패가 남아있습니다. 수동으로 확인해주세요."
    exit 0
fi

# detekt 실행 전에 카운터부터 기록한다 — 훅 timeout(300초)으로 프로세스가
# 강제 종료돼도 재시도 횟수가 보존되도록 하기 위함 (성공 시엔 아래에서 바로 지움)
RETRY_COUNT_NEXT=$((RETRY_COUNT + 1))
echo "$RETRY_COUNT_NEXT" > "$COUNTER_FILE"

DETEKT_OUTPUT=$(./gradlew detekt 2>&1)
DETEKT_EXIT=$?

if [[ "$DETEKT_EXIT" -eq 0 ]]; then
    rm -f "$COUNTER_FILE"
    exit 0
fi

# detekt 콘솔 출력 형식: <파일경로>:<line>:<col>: <메시지> [<RuleId>]
ISSUE_SUMMARY=$(echo "$DETEKT_OUTPUT" | grep -E ':[0-9]+:[0-9]+: .*\[[A-Za-z0-9]+\]$' | head -40)
[[ -z "$ISSUE_SUMMARY" ]] && ISSUE_SUMMARY=$(echo "$DETEKT_OUTPUT" | tail -60)

# ISSUE_SUMMARY는 detekt 출력(신뢰할 수 없는 텍스트)이므로 셸 문자열 보간 없이
# 파일 경유로 python에 전달해 JSON을 안전하게 생성한다 (커맨드 인젝션 방지)
SUMMARY_FILE=$(mktemp)
trap 'rm -f "$SUMMARY_FILE"' EXIT
printf '%s\n' "$ISSUE_SUMMARY" > "$SUMMARY_FILE"

python3 - "$SUMMARY_FILE" "$RETRY_COUNT_NEXT" "$MAX_RETRIES" <<'PYEOF'
import json
import sys

summary_file, retry_count, max_retries = sys.argv[1], sys.argv[2], sys.argv[3]
with open(summary_file, encoding="utf-8") as f:
    summary = f.read()

reason = (
    f"detekt 검사에 실패했습니다 (재시도 {retry_count}/{max_retries}). "
    "아래 이슈를 수정한 뒤 다시 확인해주세요.\n\n"
    f"{summary}\n"
    "전체 리포트: **/build/reports/detekt/detekt.html"
)
print(json.dumps({"decision": "block", "reason": reason}))
PYEOF
exit 0
