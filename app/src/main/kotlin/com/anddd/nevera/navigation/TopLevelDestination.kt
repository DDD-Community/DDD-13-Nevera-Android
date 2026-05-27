package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBarItem
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.mypage.navigation.MyPageRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(val route: Any) {
    Home(route = HomeRoute),

    // Todo 냉장고 추가
    MyPage(route = MyPageRoute);

    val routeClass: KClass<*> get() = route::class
}

@Composable
fun TopLevelDestination.toNavigationBarItem(
    selected: Boolean,
): NeveraNavigationBarItem<TopLevelDestination> {
    return NeveraNavigationBarItem(
        tag = this,
        selectedIcon = selectedIcon(),
        unselectedIcon = unselectedIcon(),
        selected = selected,
    )
}

@Composable
private fun TopLevelDestination.selectedIcon(): Painter = when (this) {
    TopLevelDestination.Home -> NeveraIcons.NavHomeFilled
    TopLevelDestination.MyPage -> NeveraIcons.NavMyFilled
}

@Composable
private fun TopLevelDestination.unselectedIcon(): Painter = when (this) {
    TopLevelDestination.Home -> NeveraIcons.NavHome
    TopLevelDestination.MyPage -> NeveraIcons.NavMy
}
