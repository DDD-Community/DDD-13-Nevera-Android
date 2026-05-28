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
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.GALLERY_MODE_VALUE
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen
import com.anddd.nevera.feature.ingredient.ocrerror.navigation.OcrErrorRoute
import kotlinx.serialization.Serializable

// ─── Routes ───────────────────────────────────────────────────────────────────

@Serializable
data object IngredientGraphRoute

/**
 * imageUri 프로퍼티명은 IngredientViewModel.savedStateHandle["imageUri"] 와 일치해야 합니다.
 */
@Serializable
internal data class IngredientRoute(val imageUri: String)

/** IngredientViewModel에서 savedStateHandle[ARG_IMAGE_URI]로 접근하는 키 이름과 동일 */
internal const val ARG_IMAGE_URI = "imageUri"

// ─── NavController 확장 ───────────────────────────────────────────────────────

/**
 * 홈 화면 → 영수증 촬영(카메라) 모드로 진입
 */
fun NavController.navigateToOcrCaptureCamera(
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
) {
    navigate(IngredientGraphRoute, androidx.navigation.navOptions(builder))
}

/**
 * 홈 화면 → 온라인 주문내역(갤러리) 모드로 진입
 */
fun NavController.navigateToOcrCaptureGallery(
    builder: androidx.navigation.NavOptionsBuilder.() -> Unit = {},
) {
    navigate(OcrCaptureRoute(GALLERY_MODE_VALUE), androidx.navigation.navOptions(builder))
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
    onNavigateToHome: () -> Unit, // 식재료 등록 완료화면에서 사용예정.
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
    }
}
