package com.anddd.nevera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import com.anddd.nevera.domain.usecase.deeplink.ResolveDeeplinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resolveDeeplinkUseCase: ResolveDeeplinkUseCase,
) : ViewModel() {

    private val _sideEffectChannel = Channel<DeeplinkAction>(Channel.BUFFERED)
    val sideEffect: Flow<DeeplinkAction> = _sideEffectChannel.receiveAsFlow()

    fun dispatchDeeplink(deeplink: String) {
        viewModelScope.launch {
            val action = resolveDeeplinkUseCase(deeplink)
            if (action != null) {
                _sideEffectChannel.send(action)
            } else {
                Timber.w("알 수 없는 deeplink 형식: $deeplink")
            }
        }
    }
}
