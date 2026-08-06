package com.anddd.nevera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.navigation.DeeplinkResolver
import com.anddd.nevera.navigation.DeeplinkTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deeplinkResolver: DeeplinkResolver,
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Checking)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _deeplinkTargets = Channel<DeeplinkTarget>(Channel.BUFFERED)
    val deeplinkTargets: Flow<DeeplinkTarget> = _deeplinkTargets.receiveAsFlow()

    /** 스플래시의 자동 로그인 확인이 끝났을 때 호출한다. */
    fun onAuthChecked(authenticated: Boolean) {
        _sessionState.value =
            if (authenticated) SessionState.Authenticated else SessionState.Unauthenticated
    }

    /** 로그인·회원가입 성공 시 호출한다. */
    fun onAuthenticated() {
        _sessionState.value = SessionState.Authenticated
    }

    /** 로그아웃·회원탈퇴 시 호출한다. */
    fun onSignedOut() {
        _sessionState.value = SessionState.Unauthenticated
    }

    fun dispatchDeeplink(deeplink: String) {
        val target = deeplinkResolver.resolve(deeplink)
        if (target == null) {
            Timber.w("알 수 없는 deeplink 형식: $deeplink")
            return
        }
        viewModelScope.launch { _deeplinkTargets.send(target) }
    }
}
