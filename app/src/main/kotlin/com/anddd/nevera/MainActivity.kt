package com.anddd.nevera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.home.navigation.homeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.login.main.navigation.LOGIN_ROUTE
import com.anddd.nevera.feature.login.main.navigation.loginScreen
import com.anddd.nevera.feature.splash.main.navigation.SPLASH_ROUTE
import com.anddd.nevera.feature.splash.main.navigation.splashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeveraTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    NavHost(
                        navController = navController,
                        startDestination = SPLASH_ROUTE
                    ) {
                        splashScreen(
                            onNavigateToLogin = {
                                navController.navigate(LOGIN_ROUTE) {
                                    popUpTo(SPLASH_ROUTE) { inclusive = true }
                                }
                            },
                            onNavigateToHome = { userId ->
                                navController.navigate(homeRoute(userId)) {
                                    popUpTo(SPLASH_ROUTE) { inclusive = true }
                                }
                            }
                        )
                        loginScreen(
                            onLoginSuccess = { userId ->
                                navController.navigate(homeRoute(userId)) {
                                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                                }
                            },
                            onGoogleLoginClick = {
                                // TODO: Google SDK 연동 후 LoginViewModel.snsLogin 호출
                            },
                            onKakaoLoginClick = {
                                // TODO: Kakao SDK 연동 후 LoginViewModel.snsLogin 호출
                            }
                        )
                        homeScreen()
                    }
                }
            }
        }
    }
}
