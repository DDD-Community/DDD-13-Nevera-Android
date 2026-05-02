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
fun NeveraGhostIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: NeveraButtonColor = NeveraButtonColor.Primary,
    size: NeveraButtonSize = NeveraButtonSize.Large,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(NeveraTheme.radius.xSmall),
) {
    NeveraIconButton(
        painter = painter,
        contentDescription = contentDescription,
        onClick = onClick,
        colors = NeveraTheme.colors.ghostButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraGhostIconButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonPrimaryPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Primary,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonSecondaryPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Secondary,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonDangerPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "삭제",
            onClick = {},
            color = NeveraButtonColor.Danger,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonXSmallPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.XSmall,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonSmallPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Small,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonMediumPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Medium,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonLargePreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Large,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonEnabledPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = true,
        )
    }
}

@Preview(name = "NeveraGhostIconButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraGhostIconButtonDisabledPreview() {
    NeveraTheme {
        NeveraGhostIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = false,
        )
    }
}
