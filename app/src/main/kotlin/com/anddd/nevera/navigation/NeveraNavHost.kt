package com.anddd.nevera.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anddd.nevera.core.navigation.Navigator
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.api.AuthGraphRoute
import com.anddd.nevera.feature.auth.navigation.authNavGraph
import com.anddd.nevera.feature.fridge.navigation.editFridgeIngredientScreen
import com.anddd.nevera.feature.fridge.navigation.fridgeScreen
import com.anddd.nevera.feature.ingredient.api.IngredientGraphRoute
import com.anddd.nevera.feature.ingredient.main.navigation.ingredientNavGraph
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.navigateToIngredientCapture
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.mypage.navigation.myPageNavGraph
import com.anddd.nevera.feature.notification.navigation.notificationScreen
import com.anddd.nevera.feature.splash.api.SplashRoute
import com.anddd.nevera.feature.splash.main.navigation.splashScreen

@Composable
fun NeveraNavHost(
    navController: NavHostController,
    navigator: Navigator,
    googleAuthClient: GoogleAuthClient,
    onDeeplink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        splashScreen(
            onNavigateToLogin = {
                navController.navigate(AuthGraphRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            },
            onNavigateToHome = {
                navController.navigate(HomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            }
        )
        authNavGraph(
            googleAuthClient = googleAuthClient,
            navController = navController,
            onNavigateToHome = {
                navController.navigate(HomeRoute) {
                    popUpTo(AuthGraphRoute) { inclusive = true }
                }
            }
        )
        homeScreen(
            navigator = navigator,
            onNavigateToCamera = {
                navController.navigateToIngredientCapture()
            },
            onNavigateToGallery = {
                navController.navigateToIngredientCapture(openGallery = true)
            },
        )
        fridgeScreen(
            navigator = navigator,
            onNavigateToCamera = {
                navController.navigateToIngredientCapture()
            },
            onNavigateToGallery = {
                navController.navigateToIngredientCapture(openGallery = true)
            },
        )
        editFridgeIngredientScreen(navigator = navigator)
        myPageNavGraph(
            navigator = navigator,
            onNavigateToLogin = {
                navController.navigate(AuthGraphRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                }
            },
        )
        ingredientNavGraph(
            navController = navController,
            onNavigateToHome = {
                navController.navigate(HomeRoute) {
                    popUpTo(IngredientGraphRoute) { inclusive = true }
                }
            }
        )
        notificationScreen(
            onBack = { navController.popBackStack() },
            onDeeplink = onDeeplink,
        )
    }
}