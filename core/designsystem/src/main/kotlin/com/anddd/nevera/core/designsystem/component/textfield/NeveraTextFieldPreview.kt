package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

// region — NeveraTextField (일반)

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

// region — NeveraTextField (suffix)

@Preview(name = "NeveraTextField - Positive + Suffix", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Positive_WithSuffix() {
    NeveraTheme {
        NeveraTextField(
            value = "100000",
            onValueChange = {},
            suffix = { NeveraTextFieldSuffix("원") },
            config = NeveraTextFieldConfig(
                heading = "금액",
                state = NeveraTextFieldState.Positive,
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Suffix Only (useIcon=false)", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_SuffixOnly() {
    NeveraTheme {
        NeveraTextField(
            value = "50000",
            onValueChange = {},
            useIcon = false,
            suffix = { NeveraTextFieldSuffix("원") },
            config = NeveraTextFieldConfig(heading = "금액"),
        )
    }
}

@Preview(name = "NeveraTextField - Negative + Suffix", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Negative_WithSuffix() {
    NeveraTheme {
        NeveraTextField(
            value = "0",
            onValueChange = {},
            suffix = { NeveraTextFieldSuffix("원") },
            config = NeveraTextFieldConfig(
                heading = "금액",
                state = NeveraTextFieldState.Negative,
                description = "1원 이상 입력해주세요",
            ),
        )
    }
}

@Preview(name = "NeveraTextField - Disabled + Suffix", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraTextField_Disabled_WithSuffix() {
    NeveraTheme {
        NeveraTextField(
            value = "30000",
            onValueChange = {},
            enabled = false,
            suffix = { NeveraTextFieldSuffix("원") },
            config = NeveraTextFieldConfig(heading = "금액"),
        )
    }
}

// endregion

// region — NeveraPasswordTextField

@Preview(name = "NeveraPasswordTextField - Normal", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraPasswordTextField_Normal() {
    NeveraTheme {
        NeveraPasswordTextField(
            value = "password123",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호",
                placeholder = "비밀번호를 입력하세요",
            ),
        )
    }
}

@Preview(name = "NeveraPasswordTextField - Positive", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraPasswordTextField_Positive() {
    NeveraTheme {
        NeveraPasswordTextField(
            value = "password123",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호 확인",
                state = NeveraTextFieldState.Positive,
                description = "비밀번호가 일치합니다",
            ),
        )
    }
}

@Preview(name = "NeveraPasswordTextField - Negative", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraPasswordTextField_Negative() {
    NeveraTheme {
        NeveraPasswordTextField(
            value = "pw",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "비밀번호 확인",
                state = NeveraTextFieldState.Negative,
                description = "비밀번호가 일치하지 않습니다",
            ),
        )
    }
}

// endregion

// region — NeveraEmailTextField

@Preview(name = "NeveraEmailTextField - Normal", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraEmailTextField_Normal() {
    NeveraTheme {
        NeveraEmailTextField(
            value = "",
            onValueChange = {},
            config = NeveraTextFieldConfig(
                heading = "이메일",
                placeholder = "이메일을 입력하세요",
            ),
        )
    }
}

@Preview(name = "NeveraEmailTextField - Positive", showBackground = true, widthDp = 360)
@Composable
private fun PreviewNeveraEmailTextField_Positive() {
    NeveraTheme {
        NeveraEmailTextField(
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
    val previewValue = when (config.state) {
        NeveraTextFieldState.Positive -> "올바른 값"
        NeveraTextFieldState.Negative -> "잘못된 값"
        NeveraTextFieldState.Normal -> "값"
    }
    NeveraTheme {
        NeveraTextField(
            value = previewValue,
            onValueChange = {},
            config = config,
        )
    }
}

// endregion
