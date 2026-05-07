package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

// region — 상태별 단일 Preview

@Preview(name = "NeveraTextField - Normal", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Normal() {
    NeveraTheme {
        NeveraTextField(
            value = "",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "이메일",
                placeholder = "이메일을 입력하세요",
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Positive", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Positive() {
    NeveraTheme {
        NeveraTextField(
            value = "user@example.com",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "이메일",
                state = NeveraTextFieldState.Positive,
                description = "사용 가능한 이메일입니다",
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Negative", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Negative() {
    NeveraTheme {
        NeveraTextField(
            value = "invalid-email",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "이메일",
                state = NeveraTextFieldState.Negative,
                description = "이메일 형식이 올바르지 않습니다",
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Disabled() {
    NeveraTheme {
        NeveraTextField(
            value = "user@example.com",
            onValueChange = {},
            enabled = false,
            config = NeveraTextFieldConfig(
                heading = "이메일",
                description = "수정할 수 없습니다",
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Password", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Password() {
    NeveraTheme {
        NeveraTextField(
            value = "password123",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호",
                placeholder = "비밀번호를 입력하세요",
                isPassword = true,
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Positive + Eye", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_PositiveWithEye() {
    NeveraTheme {
        NeveraTextField(
            value = "password123",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호 확인",
                state = NeveraTextFieldState.Positive,
                description = "비밀번호가 일치합니다",
                isPassword = true,
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Negative + Eye", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_NegativeWithEye() {
    NeveraTheme {
        NeveraTextField(
            value = "pw",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호 확인",
                state = NeveraTextFieldState.Negative,
                description = "비밀번호가 일치하지 않습니다",
                isPassword = true,
            ),
        )
    }
}

@Preview(name = "NeveraTextField - useIcon=false", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_NoIcon() {
    NeveraTheme {
        NeveraTextField(
            value = "입력값",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "단순 입력",
                state = NeveraTextFieldState.Negative,
                description = "아이콘 없음",
                useIcon = false,
            ),
        )
    }
}

@Preview(name = "NeveraTextField - negativeColor custom", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_NegativeCustomColor() {
    NeveraTheme {
        NeveraTextField(
            value = "",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "인증번호",
                state = NeveraTextFieldState.Negative,
                description = "인증번호가 올바르지 않습니다",
                negativeColor = NeveraTheme.colors.accentOrange,
            ),
        )
    }
}

// endregion

// region — Underline 타입 Preview

@Preview(name = "NeveraTextField Underline - Normal", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Underline_Normal() {
    NeveraTheme {
        NeveraTextField(
            value = "",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                type = NeveraTextFieldType.Underline,
                heading = "이름",
                placeholder = "이름을 입력하세요",
            ),
        )
    }
}

@Preview(name = "NeveraTextField Underline - Positive", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Underline_Positive() {
    NeveraTheme {
        NeveraTextField(
            value = "홍길동",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                type = NeveraTextFieldType.Underline,
                heading = "이름",
                state = NeveraTextFieldState.Positive,
                description = "사용 가능한 이름입니다",
            ),
        )
    }
}

@Preview(name = "NeveraTextField Underline - Negative", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Underline_Negative() {
    NeveraTheme {
        NeveraTextField(
            value = "",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                type = NeveraTextFieldType.Underline,
                heading = "이름",
                state = NeveraTextFieldState.Negative,
                description = "이름을 입력해주세요",
            ),
        )
    }
}

// endregion

// region — PreviewParameterProvider

private class NeveraTextFieldConfigProvider : PreviewParameterProvider<NeveraTextFieldConfig> {
    override val values = sequenceOf(
        NeveraTextFieldConfig(
            heading = "Normal",
            placeholder = "내용을 입력하세요",
        ),
        NeveraTextFieldConfig(
            heading = "Positive",
            state = NeveraTextFieldState.Positive,
            description = "사용 가능합니다",
        ),
        NeveraTextFieldConfig(
            heading = "Negative",
            state = NeveraTextFieldState.Negative,
            description = "형식이 올바르지 않습니다",
        ),
        NeveraTextFieldConfig(
            heading = "Password",
            isPassword = true,
            placeholder = "비밀번호를 입력하세요",
        ),
        NeveraTextFieldConfig(
            heading = "Positive + Eye",
            state = NeveraTextFieldState.Positive,
            isPassword = true,
        ),
        NeveraTextFieldConfig(
            heading = "Negative + Eye",
            state = NeveraTextFieldState.Negative,
            description = "형식이 올바르지 않습니다",
            isPassword = true,
        ),
        NeveraTextFieldConfig(
            heading = "useIcon = false",
            state = NeveraTextFieldState.Negative,
            useIcon = false,
            description = "아이콘 미사용",
        ),
        NeveraTextFieldConfig(
            heading = "Underline",
            type = NeveraTextFieldType.Underline,
            placeholder = "내용을 입력하세요",
        ),
        NeveraTextFieldConfig(
            heading = "Underline Negative",
            type = NeveraTextFieldType.Underline,
            state = NeveraTextFieldState.Negative,
            description = "형식이 올바르지 않습니다",
        ),
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextFieldParam(
    @PreviewParameter(NeveraTextFieldConfigProvider::class) config: NeveraTextFieldConfig,
) {
    NeveraTheme {
        NeveraTextField(
            value = "값",
            onValueChange = {},
            config = config,
        )
    }
}

// endregion
