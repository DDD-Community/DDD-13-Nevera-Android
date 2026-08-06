package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraFilledLeadingIconButton(
    painter: Painter,
    contentDescription: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Primary,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    NeveraLeadingIconButton(
        painter = painter,
        contentDescription = contentDescription,
        label = label,
        onClick = onClick,
        colors = NeveraTheme.colors.filledButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraFilledLeadingIconButton - Primary Large", showBackground = true)
@Composable
private fun NeveraFilledLeadingIconButtonPrimaryLargePreview() {
    NeveraTheme {
        NeveraFilledLeadingIconButton(
            painter = NeveraIcons.Plus,
            contentDescription = "",
            label = "식재료",
            onClick = {},
        )
    }
}

@Preview(name = "NeveraFilledLeadingIconButton - Primary Medium", showBackground = true)
@Composable
private fun NeveraFilledLeadingIconButtonPrimaryMediumPreview() {
    NeveraTheme {
        NeveraFilledLeadingIconButton(
            painter = NeveraIcons.Plus,
            contentDescription = "",
            label = "식재료",
            onClick = {},
            size = NeveraButtonSize.Medium,
        )
    }
}

@Preview(name = "NeveraFilledLeadingIconButton - Pill Shape", showBackground = true)
@Composable
private fun NeveraFilledLeadingIconButtonPillPreview() {
    NeveraTheme {
        NeveraFilledLeadingIconButton(
            painter = NeveraIcons.Plus,
            contentDescription = "",
            label = "식재료",
            onClick = {},
            size = NeveraButtonSize.Medium,
            shape = RoundedCornerShape(NeveraTheme.radius.max),
        )
    }
}

@Preview(name = "NeveraFilledLeadingIconButton - Disabled", showBackground = true)
@Composable
private fun NeveraFilledLeadingIconButtonDisabledPreview() {
    NeveraTheme {
        NeveraFilledLeadingIconButton(
            painter = NeveraIcons.Plus,
            contentDescription = "",
            label = "식재료",
            onClick = {},
            enabled = false,
        )
    }
}
