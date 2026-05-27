package com.anddd.nevera.feature.ingredient.registersuccess.navigation

import androidx.navigation.NavController
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterSuccessRoute(val totalCost: Int)

internal fun NavController.navigateToRegisterSuccess(
    totalCost: Int,
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
) {
    navigate(RegisterSuccessRoute(totalCost), navOptions(builder))
}