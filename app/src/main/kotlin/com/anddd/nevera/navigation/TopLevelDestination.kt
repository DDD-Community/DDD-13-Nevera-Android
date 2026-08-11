package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBarItem
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.feature.fridge.api.FridgeRoute
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.mypage.api.MyPageRoute

/** 바텀 탭. 탭 하나가 곧 목적지 하나이며, core:navigation에는 스택의 루트로 전달된다. */
enum class TopLevelDestination(val key: NavKey, val label: String) {
    Home(HomeRoute, "홈"),
    Fridge(FridgeRoute, "냉장고"),
    MyPage(MyPageRoute, "마이"),
}

@Composable
fun TopLevelDestination.toNavigationBarItem(
    selected: Boolean,
): NeveraNavigationBarItem<TopLevelDestination> = NeveraNavigationBarItem(
    tag = this,
    selectedIcon = selectedIcon(),
    unselectedIcon = unselectedIcon(),
    selected = selected,
    contentDescription = label,
)

@Composable
private fun TopLevelDestination.selectedIcon(): Painter = when (this) {
    TopLevelDestination.Home -> NeveraIcons.NavHomeFilled
    TopLevelDestination.Fridge -> NeveraIcons.NavFridgeFilled
    TopLevelDestination.MyPage -> NeveraIcons.NavMyFilled
}

@Composable
private fun TopLevelDestination.unselectedIcon(): Painter = when (this) {
    TopLevelDestination.Home -> NeveraIcons.NavHome
    TopLevelDestination.Fridge -> NeveraIcons.NavFridge
    TopLevelDestination.MyPage -> NeveraIcons.NavMy
}
