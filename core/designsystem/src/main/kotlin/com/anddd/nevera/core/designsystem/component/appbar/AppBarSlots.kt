package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * [NeveraAppBarNavigation] 값을 실제 좌측 내비게이션 UI로 변환해 표시합니다.
 *
 * 전달된 타입에 따라 뒤로가기, 닫기, 메뉴 버튼을 렌더링하거나 아무것도 표시하지 않습니다.
 *
 * @param navigation 좌측에 표시할 내비게이션 상태입니다.
 */
@Composable
internal fun AppBarNavigationSlot(navigation: NeveraAppBarNavigation) {
    when (navigation) {
        is NeveraAppBarNavigation.Back -> AppBarIconButton(
            painter = NeveraIcons.ArrowBack,
            onClick = navigation.onClick,
            contentDescription = "뒤로가기",
        )

        is NeveraAppBarNavigation.Close -> AppBarIconButton(
            painter = NeveraIcons.Close,
            onClick = navigation.onClick,
            contentDescription = "닫기",
        )

        is NeveraAppBarNavigation.Menu -> AppBarIconButton(
            painter = NeveraIcons.Menu,
            onClick = navigation.onClick,
            contentDescription = "메뉴",
        )

        NeveraAppBarNavigation.None -> Unit
    }
}

/**
 * [NeveraAppBarAction] 값을 실제 우측 액션 UI로 변환해 표시합니다.
 *
 * 아이콘 액션, 텍스트 액션, 액션 없음 상태를 AppBar 우측 영역 규칙에 맞춰 렌더링합니다.
 *
 * @param action 우측에 표시할 액션 상태입니다.
 */
@Composable
internal fun AppBarActionSlot(action: NeveraAppBarAction) {
    when (action) {
        is NeveraAppBarAction.Icons -> Row(horizontalArrangement = Arrangement.spacedBy(AppBarDefault.actionSpacing)) {
            action.items.forEach { item ->
                AppBarIconButton(
                    painter = item.painter,
                    onClick = item.onClick,
                    contentDescription = item.contentDescription,
                )
            }
        }

        is NeveraAppBarAction.Text -> AppBarTextButton(action)

        NeveraAppBarAction.None -> Unit
    }
}

/**
 * AppBar에서 사용하는 공통 아이콘 버튼을 표시합니다.
 *
 * 최소 터치 영역 기본값을 비활성화한 뒤, 디자인 시스템에서 정의한 버튼과 아이콘 크기를 적용합니다.
 *
 * @param painter 표시할 아이콘 [Painter]
 * @param onClick 버튼 클릭 시 실행할 동작입니다.
 * @param contentDescription 접근성을 위한 아이콘 설명입니다.
 */
@Composable
private fun AppBarIconButton(
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(AppBarDefault.iconButtonSize),
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(AppBarDefault.iconSize),
                tint = NeveraTheme.colors.iconPrimary
            )
        }
    }
}

/**
 * [NeveraAppBarAction.Text] 값을 실제 텍스트 액션 UI로 변환해 표시합니다.
 *
 * [NeveraAppBarAction.Text.Tone]에 따라 텍스트 색상을 결정하며, Material [androidx.compose.material3.TextButton]의
 * 최소 크기 제약 없이 텍스트와 패딩 크기만으로 버튼 영역을 구성합니다.
 *
 * @param action 표시할 텍스트 액션 상태입니다.
 */
@Composable
private fun AppBarTextButton(action: NeveraAppBarAction.Text) {
    val textColor = when (action.tone) {
        NeveraAppBarAction.Text.Tone.Primary -> NeveraTheme.colors.primaryNormal
        NeveraAppBarAction.Text.Tone.Tertiary -> NeveraTheme.colors.textTertiary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = action.onClick)
            .padding(
                horizontal = AppBarDefault.textActionHorizontalPadding,
                vertical = AppBarDefault.textActionVerticalPadding,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = action.label,
            style = NeveraTheme.typography.titleXSmall,
            color = textColor,
        )
    }
}
