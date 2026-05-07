package com.anddd.nevera.core.designsystem.component.textfield.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

internal object NeveraTextFieldDefaults {

    val BoxShape: Shape = RoundedCornerShape(NeveraTheme.spacing.padding8)

    val BoxContentPadding: PaddingValues = PaddingValues(all = NeveraTheme.spacing.padding12)

    val UnderlineContentPadding: PaddingValues = PaddingValues(
        start = NeveraTheme.spacing.padding4,
        top = NeveraTheme.spacing.padding4,
        end = NeveraTheme.spacing.padding8,
        bottom = NeveraTheme.spacing.padding4
    )

    val BorderWidth: Dp = 1.dp

    val LabelStartPadding: Dp = NeveraTheme.spacing.padding4

    val HeadingBottomGap: Dp = NeveraTheme.spacing.gap8

    val DescriptionTopGap: Dp = NeveraTheme.spacing.gap4
}
