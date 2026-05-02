package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraOutlinedIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Secondary,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    NeveraIconButton(
        painter = painter,
        contentDescription = contentDescription,
        onClick = onClick,
        colors = NeveraTheme.colors.outlinedButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraOutlinedIconButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonPrimaryPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Primary,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonSecondaryPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Secondary,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonDangerPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "삭제",
            onClick = {},
            color = NeveraButtonColor.Danger,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonXSmallPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.XSmall,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonSmallPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Small,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonMediumPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Medium,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonLargePreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Large,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonEnabledPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = true,
        )
    }
}

@Preview(name = "NeveraOutlinedIconButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraOutlinedIconButtonDisabledPreview() {
    NeveraTheme {
        NeveraOutlinedIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = false,
        )
    }
}
