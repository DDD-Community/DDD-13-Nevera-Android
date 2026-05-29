package com.anddd.nevera.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.core.net.toUri
import com.anddd.nevera.core.network.di.BaseUrl
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.IngredientApi
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse
import com.anddd.nevera.data.model.ingredient.OcrProgressDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

internal class OcrDataSourceImpl @Inject constructor(
    private val ingredientApi: IngredientApi,
    @param:ApplicationContext private val context: Context,
    okHttpClient: OkHttpClient,
    @param:BaseUrl private val baseUrl: String,
    private val gson: Gson,
) : OcrDataSource {

    // SSE 연결은 응답을 기다리는 동안 연결을 유지해야 하므로 readTimeout을 해제한다.
    private val sseClient: OkHttpClient = okHttpClient.newBuilder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    override suspend fun createOcrJob(): ApiResponse<OcrJobResponse> =
        ingredientApi.createOcrJob()

    override fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse> = callbackFlow {
        val request = Request.Builder()
            .url("${baseUrl}api/v1/ocr/progress/$jobId")
            .build()

        val progressResponseType = object : TypeToken<ApiResponse<OcrProgressDto>>() {}.type

        val eventSource = EventSources.createFactory(sseClient)
            .newEventSource(
                request = request,
                listener = object : EventSourceListener() {
                    override fun onOpen(eventSource: EventSource, response: Response) {
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
                            close(t)
                            return
                        }

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

    override suspend fun extractIngredients(
        jobId: String,
        imageUri: String,
    ): ApiResponse<List<OcrIngredientDto>> {
        val bytes = compressImageForOcr(imageUri)
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = "ocr_image.jpg",
            body = bytes.toRequestBody("image/jpeg".toMediaType()),
        )

        return ingredientApi.extractIngredients(jobId, part)
    }

    private fun compressImageForOcr(imageUri: String): ByteArray {
        val uri = imageUri.toUri()
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        var targetWidth = 0
        var targetHeight = 0

        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val size = info.size
            val scale = minOf(1f, OCR_IMAGE_MAX_LONG_EDGE.toFloat() / max(size.width, size.height))
            targetWidth = (size.width * scale).roundToInt().coerceAtLeast(1)
            targetHeight = (size.height * scale).roundToInt().coerceAtLeast(1)
            decoder.setTargetSize(targetWidth, targetHeight)
        }

        return bitmap.use { decodedBitmap ->
            decodedBitmap.compressToByteArray(targetWidth, targetHeight)
        }
    }

    private fun Bitmap.compressToByteArray(initialWidth: Int, initialHeight: Int): ByteArray {
        var currentBitmap = this
        var width = initialWidth
        var height = initialHeight
        var quality = OCR_IMAGE_INITIAL_JPEG_QUALITY

        try {
            while (true) {
                val bytes = currentBitmap.toJpegBytes(quality)
                if (bytes.size <= OCR_IMAGE_MAX_BYTES) return bytes

                if (quality > OCR_IMAGE_MIN_JPEG_QUALITY) {
                    quality = max(OCR_IMAGE_MIN_JPEG_QUALITY, quality - OCR_IMAGE_JPEG_QUALITY_STEP)
                    continue
                }

                width = (width * OCR_IMAGE_RESIZE_SCALE).roundToInt().coerceAtLeast(1)
                height = (height * OCR_IMAGE_RESIZE_SCALE).roundToInt().coerceAtLeast(1)
                val resizedBitmap = Bitmap.createScaledBitmap(currentBitmap, width, height, true)
                if (currentBitmap !== this) currentBitmap.recycle()
                currentBitmap = resizedBitmap
                quality = OCR_IMAGE_INITIAL_JPEG_QUALITY
            }
        } finally {
            if (currentBitmap !== this) currentBitmap.recycle()
        }
    }

    private fun Bitmap.toJpegBytes(quality: Int): ByteArray =
        ByteArrayOutputStream().use { outputStream ->
            val success = compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            check(success) { "JPEG compression failed (quality=$quality)" }
            outputStream.toByteArray()
        }

    private inline fun <T> Bitmap.use(block: (Bitmap) -> T): T =
        try {
            block(this)
        } finally {
            recycle()
        }

    private companion object {
        private const val OCR_IMAGE_MAX_LONG_EDGE = 1_600
        private const val OCR_IMAGE_MAX_BYTES = 1_500 * 1_024
        private const val OCR_IMAGE_INITIAL_JPEG_QUALITY = 88
        private const val OCR_IMAGE_MIN_JPEG_QUALITY = 60
        private const val OCR_IMAGE_JPEG_QUALITY_STEP = 8
        private const val OCR_IMAGE_RESIZE_SCALE = 0.85f
    }
}
