package com.anddd.nevera.core.common

sealed interface NeveraResult<out T, out E> {
    data class Success<T>(val data: T) : NeveraResult<T, Nothing>
    data class Failure<E>(val error: E) : NeveraResult<Nothing, E>
}

inline fun <T, E, R> NeveraResult<T, E>.fold(
    onSuccess: (T) -> R,
    onFailure: (E) -> R,
): R = when (this) {
    is NeveraResult.Success -> onSuccess(data)
    is NeveraResult.Failure -> onFailure(error)
}

inline fun <T, E> NeveraResult<T, E>.getOrElse(defaultValue: (E) -> T): T =
    when (this) {
        is NeveraResult.Success -> data
        is NeveraResult.Failure -> defaultValue(error)
    }

inline fun <T, E> NeveraResult<T, E>.onSuccess(action: (T) -> Unit): NeveraResult<T, E> {
    if (this is NeveraResult.Success) action(data)
    return this
}

inline fun <T, E> NeveraResult<T, E>.onFailure(action: (E) -> Unit): NeveraResult<T, E> {
    if (this is NeveraResult.Failure) action(error)
    return this
}
