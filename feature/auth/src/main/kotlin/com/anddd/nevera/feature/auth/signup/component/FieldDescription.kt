package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun FieldDescription(text: String, state: NeveraTextFieldState) {
    val color = when (state) {
        NeveraTextFieldState.Positive,
        NeveraTextFieldState.Normal -> NeveraTheme.colors.textQuaternary
        NeveraTextFieldState.Negative -> NeveraTheme.colors.statusNegativeStrong
    }
    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))
    Text(
        text = text,
        style = NeveraTheme.typography.captionLarge,
        color = color,
        modifier = Modifier.padding(start = NeveraTheme.spacing.padding4),
    )
}

@Preview(showBackground = true, name = "FieldDescription - Positive")
@Composable
private fun FieldDescriptionPositivePreview() {
    NeveraTheme {
        FieldDescription(text = "인증이 완료되었습니다.", state = NeveraTextFieldState.Positive)
    }
}

@Preview(showBackground = true, name = "FieldDescription - Negative")
@Composable
private fun FieldDescriptionNegativePreview() {
    NeveraTheme {
        FieldDescription(text = "올바르지 않은 형식입니다.", state = NeveraTextFieldState.Negative)
    }
}
