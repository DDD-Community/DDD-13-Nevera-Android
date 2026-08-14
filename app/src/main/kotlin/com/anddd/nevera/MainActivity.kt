package com.anddd.nevera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 최초 생성에서만 처리한다. 화면 회전처럼 Activity가 다시 만들어지는 경우에도
        // getIntent()는 최초의 딥링크 intent를 그대로 돌려주므로, 조건 없이 처리하면
        // 사용자가 그 사이 옮겨 둔 화면을 딥링크가 다시 덮어쓴다.
        // 실행 중 도착하는 새 딥링크는 onNewIntent로 들어오므로 이 조건과 무관하다.
        if (savedInstanceState == null) handleIntent(intent)
        setContent {
            NeveraTheme {
                NeveraApp(googleAuthClient = googleAuthClient)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.toString()?.let { mainViewModel.dispatchDeeplink(it) }
        }
    }
}
