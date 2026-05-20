package com.anddd.nevera.feature.receipt.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.receipt.R

private val ShutterButtonSize = 60.dp
private val CameraControlIconSize = 48.dp

@Composable
internal fun CameraControls(
    onSwitchToGallery: () -> Unit,
    onTakePicture: () -> Unit,
    onSwapCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(
                top = NeveraTheme.spacing.padding16,
                bottom = NeveraTheme.spacing.padding24
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
    ) {
        Text(
            text = stringResource(R.string.receipt_hint_combined),
            style = NeveraTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = NeveraTheme.spacing.padding24,
                    start = NeveraTheme.spacing.padding32,
                    end = NeveraTheme.spacing.padding32
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onSwitchToGallery,
                modifier = Modifier.clip(CircleShape),
            ) {
                Icon(
                    painter = NeveraIcons.ReceiptGallery,
                    contentDescription = stringResource(R.string.receipt_gallery_icon_description),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(CameraControlIconSize),
                )
            }

            Box(
                modifier = Modifier
                    .size(ShutterButtonSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onTakePicture),
            )

            IconButton(
                onClick = onSwapCamera,
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    painter = NeveraIcons.ReceiptCameraSwap,
                    contentDescription = stringResource(R.string.receipt_camera_swap_icon_description),
                    tint = Color.White,
                    modifier = Modifier.size(CameraControlIconSize),
                )
            }
        }
    }
}

@Preview(widthDp = 360, showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CameraControlsPreview() {
    NeveraTheme {
        CameraControls(
            onSwitchToGallery = {},
            onTakePicture = {},
            onSwapCamera = {},
        )
    }
}