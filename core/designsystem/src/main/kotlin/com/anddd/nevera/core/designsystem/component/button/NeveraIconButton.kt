package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun NeveraIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    colors: NeveraButtonColors,
    modifier: Modifier = Modifier,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor = when {
        !enabled -> colors.disabledContainerColor
        isPressed -> colors.pressedContainerColor
        else -> colors.containerColor
    }
    val contentColor = when {
        !enabled -> colors.disabledContentColor
        isPressed -> colors.pressedContentColor
        else -> colors.contentColor
    }
    val borderColor = when {
        !enabled -> colors.disabledBorderColor
        isPressed -> colors.pressedBorderColor
        else -> colors.borderColor
    }

    val iconSpec = size.iconSpec()

    Surface(
        onClick = onClick,
        modifier = modifier.requiredSize(iconSpec.size),
        enabled = enabled,
        shape = shape,
        color = containerColor,
        border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(iconSpec.iconSize)
            )
        }
    }
}
