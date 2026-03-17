package com.anddd.nevera.core.network

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.core.common.NetworkError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <T> apiCall(
    crossinline block: suspend () -> T
): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SocketTimeoutException) {
        ApiResult.Error(
            NetworkError.TimeoutError()
        )
    } catch (e: IOException) {
        ApiResult.Error(
            NetworkError.NetworkConnectionError()
        )
    } catch (e: SerializationException) {
        ApiResult.Error(
            NetworkError.SerializationError(
                message = e.message
            )
        )
    } catch (e: HttpException) {
        ApiResult.Error(
            NetworkError.HttpError(
                code = e.code(),
                message = e.message()
            )
        )
    } catch (t: Throwable) {
        ApiResult.Error(
            NetworkError.UnknownError(
                message = t.message,
                throwable = t
            )
        )
    }
}
