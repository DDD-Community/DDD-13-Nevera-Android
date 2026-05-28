package com.anddd.nevera.feature.ingredient.main.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.anddd.nevera.feature.ingredient.main.IngredientScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.OcrCaptureScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.CAMERA_MODE_VALUE
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.GALLERY_MODE_VALUE
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen
import com.anddd.nevera.feature.ingredient.ocrerror.navigation.OcrErrorRoute
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

/**
 * 홈 화면 FAB → 캡처 모드 선택 후 OcrCaptureScreen 진입
 *
 * @param mode 카메라 또는 갤러리 모드
 */
fun NavController.navigateToIngredientCapture(
    mode: OcrCaptureMode = OcrCaptureMode.Camera,
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
) {
    val modeValue = when (mode) {
        OcrCaptureMode.Camera -> CAMERA_MODE_VALUE
        OcrCaptureMode.Gallery -> GALLERY_MODE_VALUE
    }
    navigate(OcrCaptureRoute(modeValue), navOptions(builder))
}

/**
 * 영수증 캡처 화면 → 식재료 등록 화면 이동
 *
 * @param imageUri 캡처된 영수증 이미지 URI (multipart API 호출에 사용)
 */
internal fun NavController.navigateToIngredient(
    imageUri: Uri,
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
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
        composable<OcrCaptureRoute> {
            OcrCaptureScreen(
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
                onNavigateToError = { navController.navigate(OcrErrorRoute) },
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
                // 다시 시도 → 동일 imageUri로 IngredientScreen 새 인스턴스 생성 (기존 인스턴스 제거)
                onRetry = {
                    val imageUri = runCatching {
                        navController.previousBackStackEntry?.toRoute<IngredientRoute>()?.imageUri
                    }.getOrNull()

                    if (imageUri == null) {
                        navController.popBackStack()
                    } else {
                        navController.navigateToIngredient(imageUri.toUri()) {
                            popUpTo<IngredientRoute> { inclusive = true }
                        }
                    }
                },
                // X 버튼 → OcrCaptureScreen 새 인스턴스로 이동, Ingredient·OcrError 스택 제거
                onClose = {
                    navController.navigate(OcrCaptureRoute()) {
                        popUpTo<IngredientRoute> { inclusive = true }
                    }
                },
            )
        }

        composable<RegisterSuccessRoute> { backStackEntry ->
            val totalCost = backStackEntry.toRoute<RegisterSuccessRoute>().totalCost
            RegisterSuccessScreen(
                totalSavedAmount = totalCost,
                onViewFridge = onNavigateToHome,
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