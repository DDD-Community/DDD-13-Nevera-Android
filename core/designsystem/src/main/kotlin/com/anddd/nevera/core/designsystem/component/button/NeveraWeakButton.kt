package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraWeakButton(
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
        colors = NeveraTheme.colors.weakButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraWeakButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonPrimaryPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, color = NeveraButtonColor.Primary)
    }
}

@Preview(name = "NeveraWeakButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonSecondaryPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, color = NeveraButtonColor.Secondary)
    }
}

@Preview(name = "NeveraWeakButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonDangerPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "삭제", onClick = {}, color = NeveraButtonColor.Danger)
    }
}

@Preview(name = "NeveraWeakButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonXSmallPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, size = NeveraButtonSize.XSmall)
    }
}

@Preview(name = "NeveraWeakButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonSmallPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, size = NeveraButtonSize.Small)
    }
}

@Preview(name = "NeveraWeakButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonMediumPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, size = NeveraButtonSize.Medium)
    }
}

@Preview(name = "NeveraWeakButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonLargePreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, size = NeveraButtonSize.Large)
    }
}

@Preview(name = "NeveraWeakButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonEnabledPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, enabled = true)
    }
}

@Preview(name = "NeveraWeakButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakButtonDisabledPreview() {
    NeveraTheme {
        NeveraWeakButton(label = "확인", onClick = {}, enabled = false)
    }
}
