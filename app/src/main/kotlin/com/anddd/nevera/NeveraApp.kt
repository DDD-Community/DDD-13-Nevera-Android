package com.anddd.nevera

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.anddd.nevera.core.designsystem.component.navigationbar.NeveraNavigationBar
import com.anddd.nevera.core.navigation.MultiStackNavigator
import com.anddd.nevera.core.navigation.rememberNavigationState
import com.anddd.nevera.core.navigation.toEntries
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.fridge.navigation.fridgeEntry
import com.anddd.nevera.feature.ingredient.main.navigation.ingredientEntry
import com.anddd.nevera.feature.main.api.HomeRoute
import com.anddd.nevera.feature.main.home.navigation.homeEntry
import com.anddd.nevera.feature.mypage.navigation.myPageEntry
import com.anddd.nevera.feature.notification.navigation.notificationEntry
import com.anddd.nevera.navigation.PreSessionHost
import com.anddd.nevera.navigation.TopLevelDestination
import com.anddd.nevera.navigation.openDeeplink
import com.anddd.nevera.navigation.toNavigationBarItem

@Composable
fun NeveraApp(
    googleAuthClient: GoogleAuthClient,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val sessionState by mainViewModel.sessionState.collectAsState()

    // 인증 이전에는 앱 본문이 아예 존재하지 않는다.
    // 로그인하지 않은 상태로 홈·냉장고에 도달하는 경로가 코드에 없다.
    if (sessionState !is SessionState.Authenticated) {
        PreSessionHost(
            googleAuthClient = googleAuthClient,
            isChecking = sessionState is SessionState.Checking,
            onAuthChecked = mainViewModel::onAuthChecked,
            onAuthenticated = mainViewModel::onAuthenticated,
        )
        return
    }

    AuthenticatedApp(mainViewModel = mainViewModel)
}

@Composable
private fun AuthenticatedApp(mainViewModel: MainViewModel) {
    val topLevelDestinations = TopLevelDestination.entries
    val navigationState = rememberNavigationState(
        startRootKey = HomeRoute,
        rootKeys = topLevelDestinations.map { it.key }.toSet(),
    )
    val navigator = remember(navigationState) { MultiStackNavigator(navigationState) }

    // 딥링크는 항상 앱 본문 위에서 소비된다. 인증 이전에는 이 코드가 실행되지 않는다.
    LaunchedEffect(navigator) {
        mainViewModel.deeplinkTargets.collect { target ->
            navigator.openDeeplink(target)
        }
    }

    val currentTopLevelKey = navigationState.currentRootKey
    // 탭 루트에 있을 때만 바텀바를 보여준다.
    val isAtTabRoot = navigationState.currentKey == currentTopLevelKey

    val entryProvider = entryProvider {
        homeEntry(navigator)
        fridgeEntry(navigator)
        myPageEntry(navigator, onSignedOut = mainViewModel::onSignedOut)
        notificationEntry(navigator, onDeeplink = mainViewModel::dispatchDeeplink)
        ingredientEntry(navigator, onExitFlow = { navigator.navigate(HomeRoute) })
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            if (isAtTabRoot) {
                NeveraNavigationBar(
                    items = topLevelDestinations.map { destination ->
                        destination.toNavigationBarItem(selected = destination.key == currentTopLevelKey)
                    },
                    onItemClick = { destination -> navigator.navigate(destination.key) },
                )
            }
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
