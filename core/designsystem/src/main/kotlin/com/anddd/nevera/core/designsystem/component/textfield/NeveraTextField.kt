package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.anddd.nevera.core.designsystem.component.textfield.internal.NeveraBaseTextField

/**
 * Nevera 디자인 시스템의 기본 텍스트 입력 컴포넌트 — String 오버로드.
 *
 * 커서 제어가 필요 없는 일반적인 사용 케이스. 포커스 기반 자동 커서 이동이 적용된다.
 * - 포커스 획득: 커서를 텍스트 맨 뒤로 이동
 * - 포커스 해제: 커서를 맨 앞으로 이동, 커서 미노출
 *
 * 커서·선택 영역을 직접 제어해야 한다면 [TextFieldValue] 오버로드를 사용한다.
 *
 * @param value 현재 입력된 텍스트 값
 * @param onValueChange 텍스트 변경 시 호출되는 콜백
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param useIcon true이면 state에 따라 check/warning 아이콘을 trailing에 표시
 * @param suffix trailing 영역 우측에 렌더링할 추가 컨텐츠. 예: `{ NeveraTextFieldSuffix("원") }`
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useIcon: Boolean = true,
    suffix: (@Composable () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    // BasicTextField는 TextFieldValue를 요구하므로 내부에서 변환해 관리한다.
    // 커서 위치는 포커스 획득 시 맨 뒤, 해제 시 맨 앞으로 항상 재설정되므로 저장할 필요가 없다 → remember 의도적 사용.
    // 입력값은 onValueChange → ViewModel uiState → value 경로로 관리된다.
    // 외부에서 value가 변경되면 LaunchedEffect가 감지해 텍스트를 동기화하며, 커서는 새 텍스트 길이 내로 clamp한다.
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            val clampedStart = textFieldValue.selection.start.coerceAtMost(value.length)
            val clampedEnd = textFieldValue.selection.end.coerceAtMost(value.length)
            textFieldValue = TextFieldValue(text = value, selection = TextRange(clampedStart, clampedEnd))
        }
    }
    NeveraTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            if (newValue.text != value) onValueChange(newValue.text)
            textFieldValue = newValue
        },
        modifier = modifier,
        enabled = enabled,
        useIcon = useIcon,
        autoMoveCursor = true,
        suffix = suffix,
        keyboardActions = keyboardActions,
        config = config,
    )
}

/**
 * Nevera 디자인 시스템의 기본 텍스트 입력 컴포넌트 — [TextFieldValue] 오버로드.
 *
 * 커서 위치·선택 영역을 호출자가 직접 제어해야 할 때 사용한다.
 * 기본값([autoMoveCursor]=false)에서는 자동 커서 이동이 비활성화되어 전달한 [TextFieldValue]의
 * 커서 위치가 그대로 유지된다.
 *
 * @param value 커서·선택 정보를 포함한 현재 입력 상태
 * @param onValueChange 입력 상태(텍스트·커서·선택) 변경 콜백
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param useIcon true이면 state에 따라 check/warning 아이콘을 trailing에 표시
 * @param autoMoveCursor true로 설정하면 포커스 기반 자동 커서 이동 정책을 적용한다 (기본 false)
 * @param suffix trailing 영역 우측에 렌더링할 추가 컨텐츠. 예: `{ NeveraTextFieldSuffix("원") }`
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useIcon: Boolean = true,
    autoMoveCursor: Boolean = false,
    suffix: (@Composable () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    NeveraBaseTextField(
        textFieldValue = value,
        onTextFieldValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        useIcon = useIcon,
        isPassword = false,
        autoMoveCursor = autoMoveCursor,
        suffix = suffix,
        keyboardActions = keyboardActions,
        config = config,
    )
}
