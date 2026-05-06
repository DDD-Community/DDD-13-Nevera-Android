package com.anddd.nevera.core.designsystem.component.bottomsheet.internal

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * BottomSheet 계열 컴포넌트에서 공유하는 기본 디자인 값을 모아둡니다.
 * 앱 전역 토큰이 아니라 BottomSheet 전용 의미 토큰 계층입니다.
 */
internal object NeveraBottomSheetDefaults {
    val ContainerShape: Shape = RoundedCornerShape(
        topStart = NeveraTheme.radius.xLarge,
        topEnd = NeveraTheme.radius.xLarge,
    )

    val ThumbWidth: Dp = 40.dp

    val ThumbHeight: Dp = 4.dp

    val ThumbVerticalPadding: Dp
        @Composable get() = NeveraTheme.spacing.padding8

    val ThumbShape: Shape
        @Composable get() = RoundedCornerShape(NeveraTheme.radius.max)

    val ThumbColor: Color
        @Composable get() = NeveraTheme.colors.borderNormal
}
