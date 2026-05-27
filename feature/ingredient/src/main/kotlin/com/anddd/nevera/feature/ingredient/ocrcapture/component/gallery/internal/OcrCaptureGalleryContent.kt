package com.anddd.nevera.feature.ingredient.ocrcapture.component.gallery.internal

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.ocrcapture.component.PermissionDeniedDialog
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent

@Composable
internal fun OcrCaptureGalleryContent(
    images: List<Uri>,
    showPermissionDialog: Boolean,
    onIntent: (OcrCaptureIntent) -> Unit,
    onDismissPermissionDialog: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxSize()
                .background(Color.White),
        ) {
            items(images) { uri ->
                GalleryImageCell(
                    uri = uri,
                    onClick = { onIntent(OcrCaptureIntent.SelectImage(it)) },
                )
            }
        }

        if (showPermissionDialog) {
            PermissionDeniedDialog(
                onDismiss = onDismissPermissionDialog,
                onOpenSettings = onOpenSettings,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureGalleryContentPreview() {
    NeveraTheme {
        OcrCaptureGalleryContent(
            images = emptyList(),
            showPermissionDialog = false,
            onIntent = {},
            onDismissPermissionDialog = {},
            onOpenSettings = {},
        )
    }
}
