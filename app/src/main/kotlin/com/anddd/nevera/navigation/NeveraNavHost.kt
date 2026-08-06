package com.anddd.nevera.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.fridge.navigation.editFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.navigation.fridgeScreen
import com.anddd.nevera.feature.ingredient.api.IngredientGraphRoute
import com.anddd.nevera.feature.ingredient.api.OcrCaptureRoute
import com.anddd.nevera.feature.ingredient.main.navigation.ingredientNavGraph
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.mypage.navigation.myPageNavGraph
import com.anddd.nevera.feature.notification.navigation.notificationScreen

@Composable
fun NeveraNavHost(
    navController: NavHostController,
    navigator: Navigator,
    onDeeplink: (String) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        // 인증 이후 그래프의 시작점. 스플래시·로그인은 PreSessionHost가 담당한다.
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        homeScreen(
            navigator = navigator,
            onNavigateToCamera = {
                navigator.navigate(OcrCaptureRoute())
            },
            onNavigateToGallery = {
                navigator.navigate(OcrCaptureRoute(openGallery = true))
            },
        )
        fridgeScreen(
            navigator = navigator,
            onNavigateToCamera = {
                navigator.navigate(OcrCaptureRoute())
            },
            onNavigateToGallery = {
                navigator.navigate(OcrCaptureRoute(openGallery = true))
            },
        )
        editFridgeIngredientScreen(navigator = navigator)
        myPageNavGraph(
            navigator = navigator,
            onNavigateToLogin = onSignedOut,
        )
        ingredientNavGraph(
            navigator = navigator,
            // NOTE: 냉장고 탭에서 시작한 경우 [Home, Fridge, Home]이 되는 문제가 남아 있다.
            //       "홈으로 간다"인지 "왔던 곳으로 돌아간다"인지 제품 결정이 필요하다.
            onNavigateToHome = { navigator.replaceFlow(HomeRoute, clearUpTo = IngredientGraphRoute) },
        )
        notificationScreen(
            onBack = { navController.popBackStack() },
            onDeeplink = onDeeplink,
        )
    }
}