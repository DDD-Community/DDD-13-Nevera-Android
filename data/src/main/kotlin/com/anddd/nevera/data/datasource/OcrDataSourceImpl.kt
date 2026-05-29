package com.anddd.nevera.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.IngredientApi
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

internal class OcrDataSourceImpl @Inject constructor(
    private val ingredientApi: IngredientApi,
    @param:ApplicationContext private val context: Context,
) : OcrDataSource {

    override suspend fun extractIngredients(imageUri: String): ApiResponse<List<OcrIngredientDto>> {
        val bytes = compressImageForOcr(imageUri)
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = "ocr_image.jpg",
            body = requestBody,
        )
        return ingredientApi.extractIngredients(part)
    }

    private fun compressImageForOcr(imageUri: String): ByteArray {
        val uri = imageUri.toUri()
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        var targetWidth = 0
        var targetHeight = 0

        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val size = info.size
            // OCR 요청 전송 용량을 줄이기 위해 긴 변 기준 최대 크기까지만 디코딩한다.
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
                // 먼저 JPEG 품질을 낮춰 목표 용량에 맞추고, 한계 품질 이후에는 해상도를 줄인다.
                val bytes = currentBitmap.toJpegBytes(quality)
                if (bytes.size <= OCR_IMAGE_MAX_BYTES) return bytes

                if (quality > OCR_IMAGE_MIN_JPEG_QUALITY) {
                    quality = max(OCR_IMAGE_MIN_JPEG_QUALITY, quality - OCR_IMAGE_JPEG_QUALITY_STEP)
                    continue
                }

                // 최소 품질에서도 용량이 크면 비트맵을 단계적으로 축소한 뒤 품질을 다시 높여 재압축한다.
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
