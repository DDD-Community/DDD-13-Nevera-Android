package com.anddd.nevera.feature.ingredient.main.navigation

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.core.navigation.replace
import com.anddd.nevera.feature.ingredient.api.IngredientRoute
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.api.OcrErrorRoute
import com.anddd.nevera.feature.ingredient.api.PhotoDetailRoute
import com.anddd.nevera.feature.ingredient.api.RegisterSuccessRoute
import com.anddd.nevera.feature.ingredient.main.IngredientScreen
import com.anddd.nevera.feature.ingredient.ocrcapture.OcrCaptureScreen
import com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen
import com.anddd.nevera.feature.ingredient.photodetail.PhotoDetailScreen
import com.anddd.nevera.feature.ingredient.registersuccess.RegisterSuccessScreen

/**
 * 식재료 등록 흐름.
 *
 * @param onExitFlow 흐름을 벗어난다. 어디로 나갈지는 조립 지점이 정한다.
 */
fun EntryProviderScope<NavKey>.ingredientEntry(
    navigator: Navigator,
    onExitFlow: () -> Unit,
) {
    entry<OcrCaptureRoute> { key ->
        OcrCaptureScreen(
            openGallery = key.openGallery,
            // X 버튼 → 이전 화면으로 복귀
            onNavigateBack = navigator::goBack,
            // 촬영 완료 → 인식 결과로 진행. 촬영 단계로는 되돌아가지 않는다.
            onNavigateToResult = { uri: Uri ->
                navigator.replace<OcrCaptureRoute>(IngredientRoute(uri.toString()))
            },
        )
    }

    entry<IngredientRoute> { key ->
        IngredientScreen(
            imageUri = key.imageUri,
            onNavigateBack = navigator::goBack,
            onNavigateToError = { navigator.navigate(OcrErrorRoute) },
            // 등록 완료 → 완료 화면. 인식 결과로는 되돌아가지 않는다.
            onNavigateToSuccess = { totalCost ->
                navigator.replace<IngredientRoute>(RegisterSuccessRoute(totalCost))
            },
            onNavigateToPhotoDetail = { imageUri -> navigator.navigate(PhotoDetailRoute(imageUri)) },
        )
    }

    entry<OcrErrorRoute> {
        OcrErrorScreen(
            // 다시 시도 → 촬영 화면으로. 실패한 인식 결과는 스택에서 제거한다.
            onRetry = { navigator.replace<IngredientRoute>(OcrCaptureRoute()) },
            onClose = onExitFlow,
        )
    }

    entry<RegisterSuccessRoute> { key ->
        RegisterSuccessScreen(
            totalSavedAmount = key.totalCost,
            onViewFridge = onExitFlow, // TODO :: 추후 냉장고탭으로 이동
            onClose = onExitFlow,
        )
    }

    entry<PhotoDetailRoute> { key ->
        PhotoDetailScreen(
            imageUri = key.imageUri,
            onClose = navigator::goBack,
        )
    }
}
