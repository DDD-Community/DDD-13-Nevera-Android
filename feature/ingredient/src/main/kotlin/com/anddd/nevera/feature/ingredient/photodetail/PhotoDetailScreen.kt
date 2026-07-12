package com.anddd.nevera.feature.ingredient.photodetail

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import com.anddd.nevera.feature.ingredient.photodetail.component.PhotoDetailContent
import com.anddd.nevera.feature.ingredient.photodetail.model.PhotoDetailUiState

/**
 * 영수증 스캔 이미지 전체화면 상세 화면
 *
 * ViewModel 없이 navigation 인자([imageUri])만으로 구성되는 화면.
 * Screen은 뒤로가기·시스템바 처리만 담당하고 렌더링은 [PhotoDetailContent]에 위임한다.
 *
 * @param imageUri 표시할 이미지 URI. null 이면 검정 배경만 표시합니다.
 * @param onClose  X 버튼 또는 뒤로가기 시 실행할 콜백
 */
@Composable
fun PhotoDetailScreen(
    imageUri: String?,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    DarkNavigationBarEffect()

    PhotoDetailContent(
        uiState = PhotoDetailUiState(imageUri = imageUri),
        onCloseClick = onClose,
    )
}

// ─── Private Components ───────────────────────────────────────────────────────

// enableEdgeToEdge() 기본값이 라이트 모드에서 흰색 scrim(#E6FFFFFF)을 네비게이션바에 적용하므로,
// 다크 배경 화면 진입 시 투명 다크로 전환하고 이탈 시 기본값으로 복원한다.
@Composable
private fun DarkNavigationBarEffect() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val activity = view.context.findActivity() as? ComponentActivity
                ?: return@DisposableEffect onDispose { }
            activity.enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            )
            onDispose {
                activity.enableEdgeToEdge()
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
