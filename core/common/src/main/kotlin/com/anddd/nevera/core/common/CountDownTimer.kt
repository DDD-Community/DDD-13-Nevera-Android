package com.anddd.nevera.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CountdownTimer(private val scope: CoroutineScope) {

    sealed interface State {
        data object Idle : State
        data class Active(val remainingSeconds: Int, val canResend: Boolean) : State
        data object Expired : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    private var job: Job? = null

    fun start(totalSeconds: Int, canResendAfterSeconds: Int) {
        job?.cancel()
        _state.value = State.Idle
        job = scope.launch {
            countdownFlow(totalSeconds).collect { remaining ->
                _state.value = if (remaining > 0) {
                    State.Active(
                        remainingSeconds = remaining,
                        canResend = remaining <= canResendAfterSeconds
                    )
                } else {
                    State.Expired
                }
            }
        }
    }

    fun reset() {
        job?.cancel()
        _state.value = State.Idle
    }

    private fun countdownFlow(totalSeconds: Int): Flow<Int> = flow {
        for (i in totalSeconds downTo 0) {
            emit(i)
            if (i > 0) delay(1000)
        }
    }
}
