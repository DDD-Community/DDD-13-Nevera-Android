package com.anddd.nevera.domain.usecase.ingredient

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 다른 모듈(예: 딥링크 처리)에서 특정 식재료로 냉장고 화면의 포커스를 요청할 때 사용하는 이벤트 채널.
 * FridgeViewModel이 [focusRequests]를 구독해 해당 식재료로 스크롤한다.
 */
@Singleton
class IngredientFocusEventBus @Inject constructor() {

    private val focusChannel = Channel<Long>(Channel.BUFFERED)

    val focusRequests: Flow<Long> = focusChannel.receiveAsFlow()

    suspend fun requestFocus(ingredientId: Long) {
        focusChannel.send(ingredientId)
    }
}
