package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.di.OcrExtractOkHttpClient
import com.anddd.nevera.core.network.di.OcrExtractRetrofit
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.OcrProgressDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class OcrProgressDataSourceImpl @Inject constructor(
    @OcrExtractRetrofit retrofit: Retrofit,
    @OcrExtractOkHttpClient okHttpClient: OkHttpClient,
    private val gson: Gson,
) : OcrProgressDataSource {

    private val baseUrl = retrofit.baseUrl()

    // SSE 연결은 응답을 기다리는 동안 연결을 유지해야 하므로 readTimeout을 해제한다.
    private val sseClient: OkHttpClient = okHttpClient.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val progressResponseType = object : TypeToken<ApiResponse<OcrProgressDto>>() {}.type

    override fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse> = callbackFlow {
        val url = baseUrl.resolve("api/v1/ocr/progress/$jobId")
            ?: error("Failed to resolve OCR progress URL for jobId=$jobId")
        val request = Request.Builder().url(url).build()

        val eventSource = EventSources.createFactory(sseClient)
            .newEventSource(
                request = request,
                listener = object : EventSourceListener() {
                    override fun onOpen(eventSource: EventSource, response: Response) {
                        Timber.d("OCR SSE Open")
                        trySend(OcrProgressResponse.Opened)
                    }

                    override fun onEvent(
                        eventSource: EventSource,
                        id: String?,
                        type: String?,
                        data: String,
                    ) {
                        val response = runCatching {
                            gson.fromJson<ApiResponse<OcrProgressDto>>(data, progressResponseType)
                        }.getOrElse { t ->
                            Timber.w(t, "Failed to parse OCR SSE progress event")
                            close(t)
                            return
                        }

                        Timber.d(
                            "OCR SSE progress event: progress=%s, result=%s, errorCode=%s",
                            response.result?.progress,
                            response.result?.result,
                            response.error?.code,
                        )
                        trySend(OcrProgressResponse.Progress(response))
                        if ((response.result?.progress ?: 0) >= 100) close()
                    }

                    override fun onFailure(
                        eventSource: EventSource,
                        t: Throwable?,
                        response: Response?,
                    ) {
                        val cause = t ?: IOException(
                            "OCR SSE connection failed: HTTP ${response?.code ?: "unknown"}",
                        )
                        Timber.w(cause, "OCR SSE connection failed")
                        close(cause)
                    }

                    override fun onClosed(eventSource: EventSource) {
                        close()
                    }
                },
            )

        awaitClose { eventSource.cancel() }
    }
}
