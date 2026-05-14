package com.anddd.nevera.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

/**
 * Nevera MVI 아키텍처의 기반 ViewModel.
 *
 * Orbit ContainerHost를 래핑하여 Intent → Mutation → State 흐름을 강제한다.
 * 모든 feature ViewModel은 이 클래스를 상속해 [handleIntent]와 [applyMutation]을 구현한다.
 *
 * @param STATE      화면 렌더링에 사용되는 불변 UI 상태. [NeveraState] 구현체.
 * @param SIDE_EFFECT 토스트·네비게이션 등 단발성 부수 효과. [NeveraSideEffect] 구현체.
 * @param INTENT     사용자 액션을 표현하는 입력 신호. [NeveraIntent] 구현체.
 * @param MUTATION   State 변환 방식을 기술하는 단위. [NeveraMutation] 구현체.
 * @param initialState 컨테이너 생성 시 사용할 초기 상태값.
 */
abstract class NeveraViewModel<
    STATE : NeveraState,
    SIDE_EFFECT : NeveraSideEffect,
    INTENT : NeveraIntent,
    MUTATION : NeveraMutation,
>(initialState: STATE) : ViewModel(), ContainerHost<STATE, SIDE_EFFECT> {

    override val container = container<STATE, SIDE_EFFECT>(
        initialState = initialState,
        buildSettings = {
            exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        },
    )

    /**
     * UI로부터 수신한 Intent를 처리한다.
     * 내부적으로 비즈니스 로직을 수행한 뒤 [applyMutation]을 호출해 State를 갱신한다.
     */
    abstract fun handleIntent(action: INTENT)

    /**
     * [MUTATION]을 받아 현재 State를 새로운 State로 변환한다.
     * Orbit [Syntax] 스코프 안에서 실행되므로 `reduce`, `postSideEffect` 등을 사용할 수 있다.
     */
    protected abstract suspend fun Syntax<STATE, SIDE_EFFECT>.applyMutation(mutation: MUTATION)
}
