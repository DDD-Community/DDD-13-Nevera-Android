package com.anddd.nevera.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.navigation.AuthGraphRoute
import com.anddd.nevera.feature.auth.navigation.authNavGraph
import com.anddd.nevera.feature.ingredient.main.navigation.IngredientGraphRoute
import com.anddd.nevera.feature.ingredient.main.navigation.ingredientNavGraph
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.mypage.navigation.myPageNavGraph
import com.anddd.nevera.feature.splash.main.navigation.SplashRoute
import com.anddd.nevera.feature.splash.main.navigation.splashScreen

@Composable
fun NeveraNavHost(
    navController: NavHostController,
    googleAuthClient: GoogleAuthClient,
    modifier: Modifier = Modifier
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
        homeScreen()
        myPageNavGraph(
            navController = navController,
            onNavigateToLogin = {
                navController.navigate(AuthGraphRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                }
            }
        )
        ingredientNavGraph(
            navController = navController,
            onNavigateToHome = {
                navController.navigate(HomeRoute) {
                    popUpTo(IngredientGraphRoute) { inclusive = true }
                }
            }
        )
    }
}