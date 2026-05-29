package com.anddd.nevera.feature.ingredient.photodetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

@Serializable
internal data class PhotoDetailRoute(val imageUri: String)

internal fun NavController.navigateToPhotoDetail(
    imageUri: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(PhotoDetailRoute(imageUri), navOptions(builder))
}