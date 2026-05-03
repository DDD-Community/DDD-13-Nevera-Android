package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraFilledButton(
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
        colors = NeveraTheme.colors.filledButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraFilledButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonPrimaryPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, color = NeveraButtonColor.Primary)
    }
}

@Preview(name = "NeveraFilledButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonSecondaryPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, color = NeveraButtonColor.Secondary)
    }
}

@Preview(name = "NeveraFilledButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonDangerPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "삭제", onClick = {}, color = NeveraButtonColor.Danger)
    }
}

@Preview(name = "NeveraFilledButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonXSmallPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, size = NeveraButtonSize.XSmall)
    }
}

@Preview(name = "NeveraFilledButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonSmallPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, size = NeveraButtonSize.Small)
    }
}

@Preview(name = "NeveraFilledButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonMediumPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, size = NeveraButtonSize.Medium)
    }
}

@Preview(name = "NeveraFilledButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonLargePreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, size = NeveraButtonSize.Large)
    }
}

@Preview(name = "NeveraFilledButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonEnabledPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, enabled = true)
    }
}

@Preview(name = "NeveraFilledButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledButtonDisabledPreview() {
    NeveraTheme {
        NeveraFilledButton(label = "확인", onClick = {}, enabled = false)
    }
}
