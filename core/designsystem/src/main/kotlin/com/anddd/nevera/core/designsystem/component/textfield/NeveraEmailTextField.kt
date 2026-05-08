package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.anddd.nevera.core.designsystem.component.textfield.internal.NeveraBaseTextField

/**
 * 이메일 입력 전용 텍스트 필드. [KeyboardType.Email]이 고정된다.
 *
 * @param value 현재 입력된 텍스트 값
 * @param onValueChange 텍스트 변경 시 호출되는 콜백
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param keyboardActions IME 액션 이벤트 처리 콜백
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    NeveraBaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        useIcon = true,
        isPassword = false,
        keyboardActions = keyboardActions,
        // KeyboardType.Email 고정, ImeAction 등 나머지 옵션은 caller config 유지
        config = config.copy(keyboardOptions = config.keyboardOptions.copy(keyboardType = KeyboardType.Email)),
    )
}
