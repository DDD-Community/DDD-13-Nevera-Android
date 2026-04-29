package com.anddd.nevera.core.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.color.NeveraColor
import com.anddd.nevera.core.designsystem.ui.theme.shape.NeveraRadius
import com.anddd.nevera.core.designsystem.ui.theme.spacing.NeveraSpacing
import com.anddd.nevera.core.designsystem.ui.theme.typography.NeveraTypography

enum class NeveraButtonColor { Primary, Secondary }

enum class NeveraButtonStyle { Filled, Weak, Outlined, Ghost, Rounded }

enum class NeveraButtonSize { XSmall, Small, Medium, Large }

@Composable
fun NeveraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Primary,
    style: NeveraButtonStyle = NeveraButtonStyle.Filled,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    iconOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    require(!iconOnly || leadingIcon != null || trailingIcon != null) {
        "iconOnly=true 일 때 leadingIcon 또는 trailingIcon 중 하나는 반드시 제공해야 합니다."
    }
    require(iconOnly || text.isNotBlank() || leadingIcon != null || trailingIcon != null) {
        "text가 비어 있을 경우 leadingIcon 또는 trailingIcon 중 하나는 반드시 제공해야 합니다."
    }
    if (iconOnly && leadingIcon != null && trailingIcon != null) {
        Log.w("NeveraButton", "iconOnly=true일 때 leadingIcon과 trailingIcon이 둘 다 전달되었습니다. trailingIcon은 무시됩니다.")
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val sizeSpec = size.toSpec(NeveraTheme.typography)
    val colorSpec = buttonColors(color, style, isPressed)
    val shape = remember(style) {
        if (style == NeveraButtonStyle.Rounded) RoundedCornerShape(NeveraRadius.max)
        else RoundedCornerShape(NeveraRadius.small)
    }

    val baseModifier = modifier
        .then(if (iconOnly) Modifier.size(sizeSpec.height) else Modifier.heightIn(min = sizeSpec.height))
        // alpha를 clip/background보다 앞에 두어 border 포함 버튼 전체가 균일하게 dimming되도록 함
        .alpha(if (enabled) 1f else 0.3f)
        // ripple이 shape 경계 밖으로 넘치지 않도록 clip을 clickable보다 앞에 배치
        .clip(shape)
        .background(colorSpec.containerColor)
        .then(
            if (style == NeveraButtonStyle.Outlined) {
                Modifier.border(1.dp, colorSpec.borderColor, shape)
            } else {
                Modifier
            }
        )
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            enabled = enabled,
            onClick = onClick,
        )

    if (iconOnly) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = baseModifier,
        ) {
            // leadingIcon 우선, 둘 다 전달된 경우 trailingIcon은 무시됨
            (leadingIcon ?: trailingIcon)?.let { icon ->
                Box(Modifier.size(sizeSpec.iconSize)) { icon() }
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = baseModifier.padding(
                horizontal = sizeSpec.horizontalPadding,
                vertical = sizeSpec.verticalPadding,
            ),
        ) {
            leadingIcon?.let { icon ->
                Box(Modifier.size(sizeSpec.iconSize)) { icon() }
            }
            // Row gap 대신 텍스트 래퍼에 수평 패딩을 줘서 아이콘-텍스트 간격을 구현
            // (아이콘 없을 때도 동일 패딩이 적용되어 텍스트 좌우 여백이 일관되게 유지됨)
            Box(modifier = Modifier.padding(horizontal = sizeSpec.iconTextPadding)) {
                Text(
                    text = text,
                    style = sizeSpec.textStyle,
                    color = colorSpec.contentColor,
                    maxLines = 1,
                )
            }
            trailingIcon?.let { icon ->
                Box(Modifier.size(sizeSpec.iconSize)) { icon() }
            }
        }
    }
}

private data class NeveraButtonSizeSpec(
    val height: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val iconSize: Dp,
    val iconTextPadding: Dp,
    val textStyle: TextStyle,
)

private data class NeveraButtonColorSpec(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color = Color.Transparent,
)

private fun NeveraButtonSize.toSpec(typography: NeveraTypography): NeveraButtonSizeSpec {

    return when (this) {
        NeveraButtonSize.Large -> NeveraButtonSizeSpec(
            height = 48.dp,
            horizontalPadding = NeveraSpacing.padding6,
            verticalPadding = NeveraSpacing.padding5,
            iconSize = 20.dp,
            iconTextPadding = 8.dp,
            textStyle = typography.titleMedium,
        )
        NeveraButtonSize.Medium -> NeveraButtonSizeSpec(
            height = 40.dp,
            horizontalPadding = 14.dp,
            verticalPadding = 10.dp,
            iconSize = 20.dp,
            iconTextPadding = 8.dp,
            textStyle = typography.titleSmall,
        )
        NeveraButtonSize.Small -> NeveraButtonSizeSpec(
            height = 34.dp,
            horizontalPadding = 12.dp,
            verticalPadding = 8.dp,
            iconSize = 16.dp,
            iconTextPadding = 6.dp,
            textStyle = typography.titleXSmall,
        )
        NeveraButtonSize.XSmall -> NeveraButtonSizeSpec(
            height = 28.dp,
            horizontalPadding = 8.dp,
            verticalPadding = 6.dp,
            iconSize = 12.dp,
            iconTextPadding = 4.dp,
            textStyle = typography.captionMedium,
        )
    }
}

@Composable
private fun buttonColors(
    color: NeveraButtonColor,
    style: NeveraButtonStyle,
    pressed: Boolean,
): NeveraButtonColorSpec {
    val colors = NeveraTheme.colors
    return remember(color, style, pressed, colors) {
        when (color) {
            NeveraButtonColor.Primary -> primaryButtonColors(colors, style, pressed)
            NeveraButtonColor.Secondary -> secondaryButtonColors(colors, style, pressed)
        }
    }
}

private fun primaryButtonColors(
    colors: NeveraColor,
    style: NeveraButtonStyle,
    pressed: Boolean,
): NeveraButtonColorSpec = when (style) {
    NeveraButtonStyle.Filled, NeveraButtonStyle.Rounded -> NeveraButtonColorSpec(
        containerColor = if (pressed) colors.primaryStrong else colors.primaryNormal,
        contentColor = colors.textInverse,
    )
    NeveraButtonStyle.Weak -> NeveraButtonColorSpec(
        containerColor = if (pressed) colors.primaryWeak else colors.surfaceBrandPrimary,
        contentColor = colors.primaryStrong,
    )
    NeveraButtonStyle.Outlined -> NeveraButtonColorSpec(
        containerColor = colors.surfacePrimary,
        contentColor = if (pressed) colors.primaryStrong else colors.primaryNormal,
        borderColor = if (pressed) colors.primaryStrong else colors.primaryNormal,
    )
    NeveraButtonStyle.Ghost -> NeveraButtonColorSpec(
        containerColor = Color.Transparent,
        contentColor = if (pressed) colors.primaryStrong else colors.primaryNormal,
    )
}

private fun secondaryButtonColors(
    colors: NeveraColor,
    style: NeveraButtonStyle,
    pressed: Boolean,
): NeveraButtonColorSpec = when (style) {
    NeveraButtonStyle.Filled, NeveraButtonStyle.Rounded -> NeveraButtonColorSpec(
        containerColor = if (pressed) colors.secondaryStrong else colors.secondaryNormal,
        contentColor = colors.textInverse,
    )
    NeveraButtonStyle.Weak -> NeveraButtonColorSpec(
        containerColor = if (pressed) colors.surfaceTertiary else colors.surfaceSecondary,
        contentColor = colors.secondaryStrong,
    )
    NeveraButtonStyle.Outlined -> NeveraButtonColorSpec(
        containerColor = colors.surfacePrimary,
        contentColor = if (pressed) colors.secondaryStrong else colors.secondaryNormal,
        borderColor = if (pressed) colors.secondaryStrong else colors.secondaryNormal,
    )
    NeveraButtonStyle.Ghost -> NeveraButtonColorSpec(
        containerColor = Color.Transparent,
        contentColor = if (pressed) colors.secondaryStrong else colors.secondaryNormal,
    )
}

@Preview(name = "Primary - All Styles", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewPrimaryStyles() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "Filled", onClick = {}, style = NeveraButtonStyle.Filled)
            NeveraButton(text = "Weak", onClick = {}, style = NeveraButtonStyle.Weak)
            NeveraButton(text = "Outlined", onClick = {}, style = NeveraButtonStyle.Outlined)
            NeveraButton(text = "Ghost", onClick = {}, style = NeveraButtonStyle.Ghost)
            NeveraButton(text = "Rounded", onClick = {}, style = NeveraButtonStyle.Rounded)
        }
    }
}

@Preview(name = "Secondary - All Styles", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewSecondaryStyles() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "Filled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Filled)
            NeveraButton(text = "Weak", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Weak)
            NeveraButton(text = "Outlined", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Outlined)
            NeveraButton(text = "Ghost", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Ghost)
            NeveraButton(text = "Rounded", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Rounded)
        }
    }
}

@Preview(name = "All Sizes", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewSizes() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "Large", onClick = {}, size = NeveraButtonSize.Large)
            NeveraButton(text = "Medium", onClick = {}, size = NeveraButtonSize.Medium)
            NeveraButton(text = "Small", onClick = {}, size = NeveraButtonSize.Small)
            NeveraButton(text = "XSmall", onClick = {}, size = NeveraButtonSize.XSmall)
        }
    }
}

@Preview(name = "Disabled States", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewDisabled() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "Filled Disabled", onClick = {}, style = NeveraButtonStyle.Filled, enabled = false)
            NeveraButton(text = "Weak Disabled", onClick = {}, style = NeveraButtonStyle.Weak, enabled = false)
            NeveraButton(text = "Outlined Disabled", onClick = {}, style = NeveraButtonStyle.Outlined, enabled = false)
            NeveraButton(text = "Ghost Disabled", onClick = {}, style = NeveraButtonStyle.Ghost, enabled = false)
            NeveraButton(text = "Rounded Disabled", onClick = {}, style = NeveraButtonStyle.Rounded, enabled = false)
        }
    }
}

@Preview(name = "Secondary - Disabled States", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewSecondaryDisabled() {
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "Filled Disabled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Filled, enabled = false)
            NeveraButton(text = "Weak Disabled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Weak, enabled = false)
            NeveraButton(text = "Outlined Disabled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Outlined, enabled = false)
            NeveraButton(text = "Ghost Disabled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Ghost, enabled = false)
            NeveraButton(text = "Rounded Disabled", onClick = {}, color = NeveraButtonColor.Secondary, style = NeveraButtonStyle.Rounded, enabled = false)
        }
    }
}

@Preview(name = "Icon Variants", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewIconVariants() {
    val iconPlaceholder: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
        )
    }
    NeveraTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            NeveraButton(text = "No Icon", onClick = {})
            NeveraButton(text = "Leading Icon", onClick = {}, leadingIcon = iconPlaceholder)
            NeveraButton(text = "Trailing Icon", onClick = {}, trailingIcon = iconPlaceholder)
            NeveraButton(text = "", onClick = {}, iconOnly = true, leadingIcon = iconPlaceholder)
        }
    }
}
