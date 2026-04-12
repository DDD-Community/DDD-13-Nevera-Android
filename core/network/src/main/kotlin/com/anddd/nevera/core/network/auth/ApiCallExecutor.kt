package com.anddd.nevera.core.network.auth

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.network.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API 호출의 공통 에러 처리를 담당하는 실행기.
 *
 * Repository에서 직접 예외를 처리하는 대신 이 클래스에 위임하여,
 * 에러 변환 로직을 네트워크 레이어 안에 캡슐화한다.
 * Gson은 DI로 주입받아 errorBody 파싱에 재사용한다.
 */
@Singleton
class ApiCallExecutor @Inject constructor(private val gson: Gson) {

    suspend operator fun <T> invoke(block: suspend () -> ApiResponse<T>?): NeveraResult<T, NetworkError> {
        return try {
            val body = block() ?: return NeveraResult.Failure(
                NetworkError.UnknownError(message = "Empty response body")
            )

            val error = body.error
            // 200 응답이어도 body.error가 존재하면 서버 비즈니스 에러로 처리
            if (body.result == null && error != null) {
                return NeveraResult.Failure(
                    NetworkError.HttpError(
                        code = error.code ?: -1,
                        message = error.message ?: "Empty Message"
                    )
                )
            }

            val result = body.result
            if (result != null) {
                NeveraResult.Success(result)
            } else {
                NeveraResult.Failure(
                    NetworkError.UnknownError(message = "Empty result")
                )
            }
        } catch (e: CancellationException) {
            // 코루틴 취소는 재전파하여 구조화된 동시성을 유지
            throw e
        } catch (e: SocketTimeoutException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            NeveraResult.Failure(
                NetworkError.TimeoutError(throwable = e)
            )
        } catch (e: HttpException) {
            // 200 외 상태코드에서도 서버가 JSON 에러 바디를 내려주므로 파싱 시도.
            // 파싱 실패 시 HTTP 상태코드/메시지로 fallback.
            val apiError = runCatching {
                e.response()?.errorBody()?.charStream()?.use { reader ->
                    gson.fromJson(reader, ApiResponse::class.java)?.error
                }
            }.getOrNull()
            NeveraResult.Failure(
                NetworkError.HttpError(
                    code = apiError?.code ?: e.code(),
                    message = apiError?.message ?: e.message(),
                    throwable = e
                )
            )
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            NeveraResult.Failure(
                NetworkError.NetworkConnectionError(throwable = e)
            )
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            NeveraResult.Failure(
                NetworkError.UnknownError(
                    message = e.message,
                    throwable = e
                )
            )
        }
    }
}