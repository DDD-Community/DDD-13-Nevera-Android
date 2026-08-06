package com.anddd.nevera.feature.ingredient.main.navigation

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.core.navigation.replaceStep
import com.anddd.nevera.feature.ingredient.api.IngredientGraphRoute
import com.anddd.nevera.feature.ingredient.api.IngredientRoute
import com.anddd.nevera.feature.ingredient.api.OcrErrorRoute
import com.anddd.nevera.feature.ingredient.api.PhotoDetailRoute
import com.anddd.nevera.feature.ingredient.api.RegisterSuccessRoute
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.main.IngredientScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.OcrCaptureScreen
import com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen
import com.anddd.nevera.feature.ingredient.photodetail.PhotoDetailScreen
import com.anddd.nevera.feature.ingredient.registersuccess.RegisterSuccessScreen

// ─── 그래프 ────────────────────────────────────────────────────────────────────

fun NavGraphBuilder.ingredientNavGraph(
    navigator: Navigator,
    onNavigateToHome: () -> Unit,
) {
    navigation<IngredientGraphRoute>(startDestination = OcrCaptureRoute()) {
        composable<OcrCaptureRoute> { backStackEntry ->
            OcrCaptureScreen(
                openGallery = backStackEntry.toRoute<OcrCaptureRoute>().openGallery,
                // X 버튼 → 이전 화면으로 복귀
                onNavigateBack = navigator::goBack,
                // 촬영/갤러리 선택 완료 → 인식 결과로 진행. 촬영 단계로는 되돌아가지 않는다.
                onNavigateToResult = { uri: Uri ->
                    navigator.replaceStep<OcrCaptureRoute>(IngredientRoute(uri.toString()))
                },
            )
        }

        composable<IngredientRoute> { backStackEntry ->
            IngredientScreen(
                imageUri = backStackEntry.toRoute<IngredientRoute>().imageUri,
                // 뒤로가기 → 이전 화면으로 복귀
                onNavigateBack = navigator::goBack,
                // OCR 인식 실패 → 에러 화면
                onNavigateToError = { navigator.navigate(OcrErrorRoute) },
                // 등록 완료 → 완료 화면. 인식 결과로는 되돌아가지 않는다.
                onNavigateToSuccess = { totalCost ->
                    navigator.replaceStep<IngredientRoute>(RegisterSuccessRoute(totalCost))
                },
                // 영수증 썸네일 탭 → 사진 상세
                onNavigateToPhotoDetail = { imageUri ->
                    navigator.navigate(PhotoDetailRoute(imageUri))
                },
            )
        }

        composable<OcrErrorRoute> {
            OcrErrorScreen(
                // 다시 시도 → 촬영 화면으로. 실패한 인식 결과는 스택에서 제거한다.
                onRetry = { navigator.replaceStep<IngredientRoute>(OcrCaptureRoute()) },
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
                onClose = navigator::goBack,
            )
        }
    }
}
