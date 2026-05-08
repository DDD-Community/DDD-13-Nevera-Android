package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.anddd.nevera.core.designsystem.component.textfield.internal.NeveraBaseTextField

/**
 * Nevera 디자인 시스템의 기본 텍스트 입력 컴포넌트.
 *
 * @param value 현재 입력된 텍스트 값
 * @param onValueChange 텍스트 변경 시 호출되는 콜백
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    NeveraBaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        useIcon = true,
        isPassword = false,
        config = config,
    )
}

/**
 * 비밀번호 입력 전용 텍스트 필드. Eye 아이콘으로 비밀번호 표시/숨기기를 지원한다.
 *
 * @param value 현재 입력된 텍스트 값
 * @param onValueChange 텍스트 변경 시 호출되는 콜백
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    NeveraBaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        useIcon = true,
        isPassword = true,
        config = config,
    )
}

/**
 * 이메일 입력 전용 텍스트 필드. [KeyboardType.Email]이 고정된다.
 *
 * @param value 현재 입력된 텍스트 값
 * @param onValueChange 텍스트 변경 시 호출되는 콜백
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 * @param enabled false일 경우 입력 및 포커스가 불가능한 비활성 상태로 표시됨
 * @param config 타입, 상태, 레이블, 플레이스홀더 등 외관 설정을 담은 [NeveraTextFieldConfig]
 */
@Composable
fun NeveraEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    config: NeveraTextFieldConfig = NeveraTextFieldConfig(),
) {
    NeveraBaseTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        useIcon = true,
        isPassword = false,
        config = NeveraTextFieldConfig(
            type = config.type,
            state = config.state,
            heading = config.heading,
            placeholder = config.placeholder,
            description = config.description,
            negativeColor = config.negativeColor,
            singleLine = config.singleLine,
            // KeyboardType.Email 고정, ImeAction 등 나머지 옵션은 caller config 유지
            keyboardOptions = config.keyboardOptions.copy(keyboardType = KeyboardType.Email),
            keyboardActions = config.keyboardActions,
        ),
    )
}
