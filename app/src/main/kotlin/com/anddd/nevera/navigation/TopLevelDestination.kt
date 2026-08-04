package com.anddd.nevera.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBarItem
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.feature.fridge.api.FridgeRoute
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.mypage.api.MyPageGraphRoute
import com.anddd.nevera.feature.mypage.api.MyPageRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route: Any,
    private val screenRoute: Any = route,
) {
    Home(route = HomeRoute),
    Fridge(route = FridgeRoute),
    MyPage(route = MyPageGraphRoute, screenRoute = MyPageRoute);

    val routeClass: KClass<*> get() = route::class
    val screenRouteClass: KClass<*> get() = screenRoute::class
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
        contentDescription = contentDescription(),
    )
}

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

private fun TopLevelDestination.contentDescription(): String = when (this) {
    TopLevelDestination.Home -> "홈"
    TopLevelDestination.Fridge -> "냉장고"
    TopLevelDestination.MyPage -> "마이페이지"
}
