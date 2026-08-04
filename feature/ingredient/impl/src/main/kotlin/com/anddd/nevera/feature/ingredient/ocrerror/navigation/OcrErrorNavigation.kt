package com.anddd.nevera.feature.ingredient.ocrerror.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

@Serializable
internal data object OcrErrorRoute

internal fun NavController.navigateToOcrError(
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrErrorRoute, navOptions(builder))
}
