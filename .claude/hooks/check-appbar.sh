#!/bin/bash
# PostToolUse hook: Scaffold의 topBar에 Nevera 디자인 시스템 AppBar 사용 여부 검사

INPUT=$(cat)
FILE=$(echo "$INPUT" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(data.get('tool_input', {}).get('file_path', ''))
" 2>/dev/null || echo "")

# .kt 파일만 검사
[[ "$FILE" != *.kt ]] && exit 0
[[ ! -f "$FILE" ]] && exit 0

# Scaffold가 없으면 패스
grep -q "Scaffold(" "$FILE" || exit 0

# topBar가 있는데 Nevera AppBar를 쓰지 않으면 경고
if grep -q "topBar" "$FILE"; then
    if ! grep -qE "Nevera(Display|Logo|Search)?AppBar" "$FILE"; then
        echo "[디자인 시스템 위반] $FILE"
        echo ""
        echo "Scaffold의 topBar에 Material3 기본 AppBar 대신 Nevera 디자인 시스템 AppBar를 사용해야 합니다."
        echo ""
        echo "허용된 컴포넌트:"
        echo "  - NeveraAppBar        : 일반 상세·설정 화면"
        echo "  - NeveraDisplayAppBar : 섹션 진입·타이틀 강조 화면"
        echo "  - NeveraLogoAppBar    : 메인·브랜드 화면"
        echo "  - NeveraSearchAppBar  : 검색 화면"
        exit 2
    fi
fi
