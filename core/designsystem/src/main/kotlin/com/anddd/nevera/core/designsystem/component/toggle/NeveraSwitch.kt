package com.anddd.nevera.core.designsystem.component.toggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

private val TRACK_WIDTH = 52.dp
private val TRACK_HEIGHT = 28.dp
private val THUMB_SIZE = 22.dp
private val THUMB_PADDING = (TRACK_HEIGHT - THUMB_SIZE) / 2

@Composable
fun NeveraSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TRACK_WIDTH - THUMB_SIZE - THUMB_PADDING else THUMB_PADDING,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "thumbOffset",
    )

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> NeveraTheme.colors.surfaceTertiary
            checked -> NeveraTheme.colors.primaryNormal
            else -> NeveraTheme.colors.surfaceQuaternary
        },
        label = "trackColor",
    )

    Box(
        modifier = modifier
            .size(TRACK_WIDTH, TRACK_HEIGHT)
            .clip(RoundedCornerShape(NeveraTheme.radius.max))
            .background(trackColor)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = onCheckedChange,
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(THUMB_SIZE)
                .clip(CircleShape)
                .background(NeveraTheme.colors.surfacePrimary),
        )
    }
}

@Preview(name = "NeveraSwitch - ON", showBackground = true)
@Composable
private fun NeveraSwitchOnPreview() {
    NeveraTheme {
        NeveraSwitch(checked = true, onCheckedChange = {})
    }
}

@Preview(name = "NeveraSwitch - OFF", showBackground = true)
@Composable
private fun NeveraSwitchOffPreview() {
    NeveraTheme {
        NeveraSwitch(checked = false, onCheckedChange = {})
    }
}

@Preview(name = "NeveraSwitch - Disabled", showBackground = true)
@Composable
private fun NeveraSwitchDisabledPreview() {
    NeveraTheme {
        NeveraSwitch(checked = false, onCheckedChange = {}, enabled = false)
    }
}
