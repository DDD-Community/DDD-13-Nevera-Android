package com.anddd.nevera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import com.anddd.nevera.domain.usecase.deeplink.ResolveDeeplinkUseCase
import com.anddd.nevera.domain.usecase.ingredient.RequestIngredientFocusUseCase
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
    private val requestIngredientFocus: RequestIngredientFocusUseCase,
) : ViewModel() {

    private val _sideEffectChannel = Channel<DeeplinkAction>(Channel.BUFFERED)
    val sideEffect: Flow<DeeplinkAction> = _sideEffectChannel.receiveAsFlow()

    fun dispatchDeeplink(deeplink: String) {
        viewModelScope.launch {
            when (val action = resolveDeeplinkUseCase(deeplink)) {
                is DeeplinkAction.NavigateToIngredientDetail -> {
                    _sideEffectChannel.send(action)
                    dispatchIngredientFocusRequest(action)
                }

                null -> Timber.w("알 수 없는 deeplink 형식: $deeplink")
            }
        }
    }

    private suspend fun dispatchIngredientFocusRequest(action: DeeplinkAction.NavigateToIngredientDetail) {
        // 냉장고 화면(FridgeViewModel)으로 직접 연결할 navigation 경로가 없으므로,
        // RequestIngredientFocusUseCase를 통해 해당 식재료로 스크롤하도록 요청한다.
        val ingredientId = action.ingredientId.toLongOrNull() ?: return
        requestIngredientFocus(ingredientId)
    }
}
