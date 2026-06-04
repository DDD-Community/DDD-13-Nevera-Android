package com.anddd.nevera.feature.ingredient.main.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.anddd.nevera.feature.ingredient.main.IngredientScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.OcrCaptureScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.navigateToIngredientCapture
import com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen
import com.anddd.nevera.feature.ingredient.ocrerror.navigation.OcrErrorRoute
import com.anddd.nevera.feature.ingredient.ocrerror.navigation.navigateToOcrError
import com.anddd.nevera.feature.ingredient.photodetail.PhotoDetailScreen
import com.anddd.nevera.feature.ingredient.photodetail.navigation.PhotoDetailRoute
import com.anddd.nevera.feature.ingredient.photodetail.navigation.navigateToPhotoDetail
import com.anddd.nevera.feature.ingredient.registersuccess.RegisterSuccessScreen
import com.anddd.nevera.feature.ingredient.registersuccess.navigation.RegisterSuccessRoute
import com.anddd.nevera.feature.ingredient.registersuccess.navigation.navigateToRegisterSuccess
import kotlinx.serialization.Serializable

// ─── Routes ───────────────────────────────────────────────────────────────────

@Serializable
data object IngredientGraphRoute

@Serializable
internal data class IngredientRoute(val imageUri: String)

// ─── NavController 확장 ───────────────────────────────────────────────────────

internal fun NavController.navigateToIngredient(
    imageUri: Uri,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = IngredientRoute(imageUri.toString()),
        navOptions = navOptions(builder)
    )
}

// ─── 그래프 ────────────────────────────────────────────────────────────────────

fun NavGraphBuilder.ingredientNavGraph(
    navController: NavController,
    onNavigateToHome: () -> Unit,
) {
    navigation<IngredientGraphRoute>(startDestination = OcrCaptureRoute()) {
        composable<OcrCaptureRoute> { backStackEntry ->
            val openGallery = backStackEntry.toRoute<OcrCaptureRoute>().openGallery
            OcrCaptureScreen(
                openGallery = openGallery,
                // X 버튼 → 이전 화면으로 복귀
                onNavigateBack = { navController.popBackStack() },
                // 촬영/갤러리 선택 완료 → IngredientScreen으로 이동, OcrCaptureScreen은 스택에서 제거
                onNavigateToResult = { uri: Uri ->
                    navController.navigateToIngredient(uri) {
                        popUpTo<OcrCaptureRoute> { inclusive = true }
                    }
                },
            )
        }

        composable<IngredientRoute> {
            IngredientScreen(
                // 뒤로가기 → 이전 화면으로 복귀
                onNavigateBack = { navController.popBackStack() },
                // OCR 인식 실패 → OcrErrorScreen으로 이동
                onNavigateToError = { navController.navigateToOcrError() },
                // 등록 완료 → RegisterSuccessScreen으로 이동, Ingredient 스택 제거
                onNavigateToSuccess = { totalCost ->
                    navController.navigateToRegisterSuccess(totalCost) {
                        popUpTo<IngredientRoute> { inclusive = true }
                    }
                },
                // 영수증 썸네일 탭 → PhotoDetailScreen으로 이동
                onNavigateToPhotoDetail = { imageUri ->
                    navController.navigateToPhotoDetail(imageUri)
                },
            )
        }

        composable<OcrErrorRoute> {
            OcrErrorScreen(
                // 다시 시도 → OcrCaptureScreen 복귀, Ingredient·OcrError 스택 제거
                onRetry = {
                    navController.navigateToIngredientCapture {
                        popUpTo<IngredientRoute> { inclusive = true }
                    }
                },
                // X 버튼 → 홈 화면으로 이동
                onClose = onNavigateToHome,
            )
        }

        composable<RegisterSuccessRoute> { backStackEntry ->
            val totalCost = backStackEntry.toRoute<RegisterSuccessRoute>().totalCost
            RegisterSuccessScreen(
                totalSavedAmount = totalCost,
                onViewFridge = onNavigateToHome, // TODO :: 추후 냉장고탭으로 이동
                onClose = onNavigateToHome,
            )
        }

        composable<PhotoDetailRoute> { backStackEntry ->
            val imageUri = backStackEntry.toRoute<PhotoDetailRoute>().imageUri
            PhotoDetailScreen(
                imageUri = imageUri,
                onClose = { navController.popBackStack() },
            )
        }
    }
}