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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.anddd.nevera.core.designsystem.component.textfield.internal.NeveraBaseTextField

/**
 * 비밀번호 입력 전용 텍스트 필드 — String 오버로드.
 * Eye 아이콘 토글로 비밀번호 표시/숨기기를 지원하며, [KeyboardType.Password]가 고정된다.
 *
 * 포커스 기반 자동 커서 이동이 적용된다 (획득 시 맨 뒤, 해제 시 맨 앞).
 * 커서를 직접 제어해야 한다면 [TextFieldValue] 오버로드를 사용한다.
 *
 * @param useIcon true이면 state 아이콘(check/warning)과 eye 아이콘을 trailing에 표시
 * @param suffix trailing 영역 우측에 렌더링할 추가 컨텐츠. 예: `{ NeveraTextFieldSuffix("원") }`
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    useIcon: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
    suffix: (@Composable () -> Unit)? = null,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            val clampedStart = textFieldValue.selection.start.coerceAtMost(value.length)
            val clampedEnd = textFieldValue.selection.end.coerceAtMost(value.length)
            textFieldValue = TextFieldValue(text = value, selection = TextRange(clampedStart, clampedEnd))
        }
    }
    NeveraPasswordTextField(
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
 * 비밀번호 입력 전용 텍스트 필드 — [TextFieldValue] 오버로드.
 *
 * 커서 위치·선택 영역을 호출자가 직접 제어할 때 사용한다.
 * [autoMoveCursor]=false(기본)이면 자동 커서 이동이 비활성화된다.
 *
 * @param autoMoveCursor true로 설정하면 포커스 기반 자동 커서 이동 정책을 적용한다 (기본 false)
 * @param suffix trailing 영역 우측에 렌더링할 추가 컨텐츠. 예: `{ NeveraTextFieldSuffix("원") }`
 */
@Composable
fun NeveraPasswordTextField(
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
        isPassword = true,
        autoMoveCursor = autoMoveCursor,
        suffix = suffix,
        keyboardActions = keyboardActions,
        // KeyboardType.Password 고정으로 OS 키보드 자동완성/추천 입력 비활성화
        config = config.copy(
            keyboardOptions = config.keyboardOptions.copy(
                keyboardType = KeyboardType.Password
            )
        ),
    )
}
