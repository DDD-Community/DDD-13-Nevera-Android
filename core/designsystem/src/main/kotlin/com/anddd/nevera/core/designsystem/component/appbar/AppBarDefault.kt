package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.iconsize.NeveraIconSize
import com.anddd.nevera.core.designsystem.ui.theme.spacing.NeveraSpacing

/**
 * AppBar 구성 요소에서 공통으로 사용하는 기본 치수와 간격 값을 제공합니다.
 *
 * 개별 AppBar 구현이 동일한 높이, 패딩, 아이콘 크기를 유지하도록 할 때 사용합니다.
 */
internal object AppBarDefault {
    val height = 56.dp
    val horizontalSpacingMedium = NeveraSpacing.medium
    val horizontalSpacingLarge = NeveraSpacing.base
    val actionSpacing = NeveraSpacing.xSmall
    val iconButtonSize = NeveraIconSize.large
    val iconSize = NeveraIconSize.medium
}
