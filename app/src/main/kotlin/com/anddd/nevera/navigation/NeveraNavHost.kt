package com.anddd.nevera.navigation

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import timber.log.Timber
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.AuthGraphRoute
import com.anddd.nevera.feature.auth.navigation.authNavGraph
import com.anddd.nevera.feature.fridge.main.navigation.fridgeScreen
import com.anddd.nevera.feature.ingredient.main.navigation.IngredientGraphRoute
import com.anddd.nevera.feature.ingredient.main.navigation.ingredientNavGraph
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.navigateToIngredientCapture
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.mypage.navigation.myPageNavGraph
import com.anddd.nevera.feature.notification.navigation.NotificationRoute
import com.anddd.nevera.feature.notification.navigation.notificationScreen
import com.anddd.nevera.feature.splash.main.navigation.SplashRoute
import com.anddd.nevera.feature.splash.main.navigation.splashScreen

@Composable
fun NeveraNavHost(
    navController: NavHostController,
    googleAuthClient: GoogleAuthClient,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            onNavigateToCamera = {
                navController.navigateToIngredientCapture(OcrCaptureMode.Camera)
            },
            onNavigateToGallery = {
                navController.navigateToIngredientCapture(OcrCaptureMode.Gallery)
            },
            onNavigateToNotification = {
                navController.navigate(NotificationRoute) { launchSingleTop = true }
            },
        )
        fridgeScreen(
            onNavigateToCamera = {
                navController.navigateToIngredientCapture(OcrCaptureMode.Camera)
            },
            onNavigateToGallery = {
                navController.navigateToIngredientCapture(OcrCaptureMode.Gallery)
            },
            onNavigateToNotification = {
                navController.navigate(NotificationRoute) { launchSingleTop = true }
            },
        )
        myPageNavGraph(
            navController = navController,
            onNavigateToLogin = {
                navController.navigate(AuthGraphRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                }
            },
            onNavigateToNotification = {
                navController.navigate(NotificationRoute) { launchSingleTop = true }
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
            onDeeplink = { deeplink ->
                when {
                    deeplink.startsWith("nevera://detail/") -> {
                        val ingredientId = deeplink.removePrefix("nevera://detail/")
                        if (ingredientId.isNotBlank()) {
                            // TODO: 냉장고 탭 구현 후 식재료(id=$ingredientId) 포커스 네비게이션 추가
                            Toast.makeText(context, "냉장고 탭으로 이동", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> Timber.w("알 수 없는 deeplink 형식: $deeplink")
                }
            },
        )
    }
}