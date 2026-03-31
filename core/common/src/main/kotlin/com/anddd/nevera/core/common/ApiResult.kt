package com.anddd.nevera.core.common

sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T
    ) : ApiResult<T>

    data class Error(
        val error: NetworkError
    ) : ApiResult<Nothing>
}

inline fun <T, R> ApiResult<T>.mapSuccess(transform: (T) -> R): ApiResult<R> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> this
    }