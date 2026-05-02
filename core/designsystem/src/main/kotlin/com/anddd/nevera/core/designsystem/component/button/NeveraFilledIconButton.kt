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
fun NeveraFilledIconButton(
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
        colors = NeveraTheme.colors.filledButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraFilledIconButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonPrimaryPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Primary,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonSecondaryPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Secondary,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonDangerPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "삭제",
            onClick = {},
            color = NeveraButtonColor.Danger,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonXSmallPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.XSmall,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonSmallPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Small,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonMediumPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Medium,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonLargePreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Large,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonEnabledPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = true,
        )
    }
}

@Preview(name = "NeveraFilledIconButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraFilledIconButtonDisabledPreview() {
    NeveraTheme {
        NeveraFilledIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = false,
        )
    }
}
