package com.anddd.nevera.core.common

sealed interface NeveraResult<out T, out E> {
    data class Success<T>(val data: T) : NeveraResult<T, Nothing>
    data class Failure<E>(val error: E) : NeveraResult<Nothing, E>
}

inline fun <T, E> NeveraResult<T, E>.onSuccess(action: (T) -> Unit): NeveraResult<T, E> {
    if (this is NeveraResult.Success) action(data)
    return this
}

inline fun <T, E> NeveraResult<T, E>.onFailure(action: (E) -> Unit): NeveraResult<T, E> {
    if (this is NeveraResult.Failure) action(error)
    return this
}

inline fun <T, E, R> NeveraResult<T, E>.mapSuccess(
    transform: (T) -> R,
): NeveraResult<R, E> = when (this) {
    is NeveraResult.Success -> NeveraResult.Success(transform(data))
    is NeveraResult.Failure -> NeveraResult.Failure(error)
}

inline fun <T, E, F> NeveraResult<T, E>.mapFailure(
    transform: (E) -> F,
): NeveraResult<T, F> = when (this) {
    is NeveraResult.Success -> NeveraResult.Success(data)
    is NeveraResult.Failure -> NeveraResult.Failure(transform(error))
}

inline fun <T, E, R, F> NeveraResult<T, E>.map(
    onSuccess: (T) -> R,
    onFailure: (E) -> F,
): NeveraResult<R, F> = when (this) {
    is NeveraResult.Success -> NeveraResult.Success(onSuccess(data))
    is NeveraResult.Failure -> NeveraResult.Failure(onFailure(error))
}
