package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraGhostButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Primary,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    NeveraButton(
        label = label,
        onClick = onClick,
        colors = NeveraTheme.colors.ghostButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraGhostButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonPrimaryPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, color = NeveraButtonColor.Primary)
    }
}

@Preview(name = "NeveraGhostButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonSecondaryPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, color = NeveraButtonColor.Secondary)
    }
}

@Preview(name = "NeveraGhostButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonDangerPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "삭제", onClick = {}, color = NeveraButtonColor.Danger)
    }
}

@Preview(name = "NeveraGhostButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonXSmallPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, size = NeveraButtonSize.XSmall)
    }
}

@Preview(name = "NeveraGhostButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonSmallPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, size = NeveraButtonSize.Small)
    }
}

@Preview(name = "NeveraGhostButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonMediumPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, size = NeveraButtonSize.Medium)
    }
}

@Preview(name = "NeveraGhostButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonLargePreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, size = NeveraButtonSize.Large)
    }
}

@Preview(name = "NeveraGhostButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonEnabledPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, enabled = true)
    }
}

@Preview(name = "NeveraGhostButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostButtonDisabledPreview() {
    NeveraTheme {
        NeveraGhostButton(label = "확인", onClick = {}, enabled = false)
    }
}
