package com.anddd.nevera.feature.ingredient.ocrcapture.component.gallery.internal

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

@Composable
internal fun GalleryImageCell(
    uri: Uri?,
    onClick: (uri: Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = uri != null) {
                onClick(uri!!)
            }
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.ocr_capture_gallery_image_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(name = "GalleryImageCell - Placeholder", showBackground = true, widthDp = 120, heightDp = 120)
@Composable
private fun GalleryImageCellPlaceholderPreview() {
    NeveraTheme {
        GalleryImageCell(
            uri = null,
            onClick = {},
        )
    }
}

@Preview(name = "GalleryImageCell - Loaded", showBackground = true, widthDp = 120, heightDp = 120)
@Composable
private fun GalleryImageCellLoadedPreview() {
    NeveraTheme {
        GalleryImageCell(
            uri = Uri.EMPTY,
            onClick = {},
        )
    }
}
