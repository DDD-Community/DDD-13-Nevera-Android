package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * NeveraTextField의 [suffix] 파라미터에 사용하는 표준 텍스트 suffix 컴포넌트.
 *
 * trailing 영역 우측에 고정 텍스트(예: "원", "%")를 표시할 때 사용한다.
 * 스타일(typography, color, padding)이 캡슐화되어 있어 사용 측에서 토큰을 직접 지정하지 않아도 된다.
 *
 * 사용 예:
 * ```kotlin
 * NeveraTextField(
 *     value = amount,
 *     onValueChange = { amount = it },
 *     suffix = { NeveraTextFieldSuffix("원") },
 * )
 * ```
 */
@Composable
fun NeveraTextFieldSuffix(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(start = NeveraTheme.spacing.padding4),
        style = NeveraTheme.typography.titleLarge,
        color = NeveraTheme.colors.textQuaternary,
    )
}
