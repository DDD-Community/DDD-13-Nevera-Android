package com.anddd.nevera.feature.ingredient.main.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anddd.nevera.feature.ingredient.main.IngredientScreen

const val INGREDIENT_ROUTE = "ingredient"
internal const val ARG_IMAGE_URI = "imageUri"

/**
 * 영수증 캡처 화면 → 식재료 등록 화면 이동
 *
 * @param imageUri 캡처된 영수증 이미지 URI (multipart API 호출에 사용)
 */
fun NavController.navigateToIngredient(imageUri: String) {
    navigate("$INGREDIENT_ROUTE?$ARG_IMAGE_URI=${Uri.encode(imageUri)}")
}

/**
 * TODO :: IngredientScreen 개발 완료 이후 MainActivity NavHost에 호출 필요.
 * 지금은 viewModel에서 uri 검증때문에 크래시 발생 가능
 */
fun NavGraphBuilder.ingredientScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$INGREDIENT_ROUTE?$ARG_IMAGE_URI={$ARG_IMAGE_URI}",
        arguments = listOf(
            navArgument(ARG_IMAGE_URI) { type = NavType.StringType },
        ),
    ) {
        IngredientScreen(onNavigateBack = onNavigateBack)
    }
}
