package com.anddd.nevera.core.common

sealed interface ApiResult<out T> {
    data class Success<T>(
        val data: T
    ) : ApiResult<T>

    data class Error(
        val error: NetworkError
    ) : ApiResult<Nothing>
}