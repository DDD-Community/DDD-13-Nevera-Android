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
fun NeveraWeakIconButton(
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
        colors = NeveraTheme.colors.weakButtonColors(color),
        modifier = modifier,
        size = size,
        enabled = enabled,
        shape = shape,
    )
}

@Preview(name = "NeveraWeakIconButton - Primary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonPrimaryPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Primary,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Secondary", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonSecondaryPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            color = NeveraButtonColor.Secondary,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Danger", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonDangerPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "삭제",
            onClick = {},
            color = NeveraButtonColor.Danger,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - XSmall", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonXSmallPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.XSmall,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Small", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonSmallPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Small,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Medium", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonMediumPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Medium,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Large", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonLargePreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            size = NeveraButtonSize.Large,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Enabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonEnabledPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = true,
        )
    }
}

@Preview(name = "NeveraWeakIconButton - Disabled", showBackground = true, widthDp = 360)
@Composable
private fun NeveraWeakIconButtonDisabledPreview() {
    NeveraTheme {
        NeveraWeakIconButton(
            painter = rememberVectorPainter(Icons.Default.Add),
            contentDescription = "추가",
            onClick = {},
            enabled = false,
        )
    }
}
