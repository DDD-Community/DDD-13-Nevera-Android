package com.anddd.nevera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.main.navigation.LoginRoute
import com.anddd.nevera.feature.auth.main.navigation.loginScreen
import com.anddd.nevera.feature.auth.signup.navigation.SignupRoute
import com.anddd.nevera.feature.auth.signup.navigation.signupScreen
import com.anddd.nevera.feature.main.home.navigation.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeScreen
import com.anddd.nevera.feature.mypage.appinfo.navigation.AppInfoRoute
import com.anddd.nevera.feature.mypage.appinfo.navigation.appInfoScreen
import com.anddd.nevera.feature.mypage.main.navigation.myPageScreen
import com.anddd.nevera.feature.mypage.settingaccount.navigation.SettingAccountRoute
import com.anddd.nevera.feature.mypage.settingaccount.navigation.settingAccountScreen
import com.anddd.nevera.feature.splash.main.navigation.SplashRoute
import com.anddd.nevera.feature.splash.main.navigation.splashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeveraTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    NavHost(
                        navController = navController,
                        startDestination = SplashRoute
                    ) {
                        splashScreen(
                            onNavigateToLogin = {
                                navController.navigate(LoginRoute) {
                                    popUpTo<SplashRoute> { inclusive = true }
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(HomeRoute) {
                                    popUpTo<SplashRoute> { inclusive = true }
                                }
                            }
                        )
                        loginScreen(
                            onNavigateToHome = {
                                navController.navigate(HomeRoute) {
                                    popUpTo<LoginRoute> { inclusive = true }
                                }
                            },
                            onNavigateToSignup = {
                                navController.navigate(SignupRoute)
                            },
                            googleAuthClient = googleAuthClient,
                        )
                        signupScreen(
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                        homeScreen()
                        myPageScreen(
                            onNavigateToAppInfo = {
                                navController.navigate(AppInfoRoute)
                            },
                            onNavigateToAccountSetting = {
                                navController.navigate(SettingAccountRoute)
                            }
                        )
                        appInfoScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                        settingAccountScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLogin = {
                                navController.navigate(LoginRoute) {
                                    popUpTo<HomeRoute> { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
