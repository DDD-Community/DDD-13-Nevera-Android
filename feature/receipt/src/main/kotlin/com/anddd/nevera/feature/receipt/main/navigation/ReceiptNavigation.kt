package com.anddd.nevera.feature.receipt.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anddd.nevera.feature.receipt.main.ReceiptScreen

const val RECEIPT_ROUTE = "receipt"
internal const val RECEIPT_INITIAL_MODE_ARG = "initialMode"
internal const val GALLERY_MODE_VALUE = "gallery"

val ORDER_CAPTURE_ROUTE = "$RECEIPT_ROUTE?$RECEIPT_INITIAL_MODE_ARG=$GALLERY_MODE_VALUE"

fun NavGraphBuilder.receiptScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$RECEIPT_ROUTE?$RECEIPT_INITIAL_MODE_ARG={$RECEIPT_INITIAL_MODE_ARG}",
        arguments = listOf(
            navArgument(RECEIPT_INITIAL_MODE_ARG) {
                type = NavType.StringType
                defaultValue = ""
            }
        ),
    ) {
        ReceiptScreen(onNavigateBack = onNavigateBack)
    }
}