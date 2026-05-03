package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Secondary,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    NeveraButton(
        label = label,
        onClick = onClick,
        colors = NeveraTheme.colors.outlinedButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraOutlinedButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonPrimaryPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, color = NeveraButtonColor.Primary)
    }
}

@Preview(name = "NeveraOutlinedButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonSecondaryPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, color = NeveraButtonColor.Secondary)
    }
}

@Preview(name = "NeveraOutlinedButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonDangerPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "삭제", onClick = {}, color = NeveraButtonColor.Danger)
    }
}

@Preview(name = "NeveraOutlinedButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonXSmallPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, size = NeveraButtonSize.XSmall)
    }
}

@Preview(name = "NeveraOutlinedButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonSmallPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, size = NeveraButtonSize.Small)
    }
}

@Preview(name = "NeveraOutlinedButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonMediumPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, size = NeveraButtonSize.Medium)
    }
}

@Preview(name = "NeveraOutlinedButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonLargePreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, size = NeveraButtonSize.Large)
    }
}

@Preview(name = "NeveraOutlinedButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonEnabledPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, enabled = true)
    }
}

@Preview(name = "NeveraOutlinedButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedButtonDisabledPreview() {
    NeveraTheme {
        NeveraOutlinedButton(label = "확인", onClick = {}, enabled = false)
    }
}
