package com.anddd.nevera.data.datasource

import android.content.Context
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.OcrApi
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import androidx.core.net.toUri

internal class OcrDataSourceImpl @Inject constructor(
    private val ocrApi: OcrApi,
    @param:ApplicationContext private val context: Context,
) : OcrDataSource {

    override suspend fun extractIngredients(imageUri: String): ApiResponse<List<OcrIngredientDto>> {
        val uri = imageUri.toUri()
        val inputStream = requireNotNull(context.contentResolver.openInputStream(uri)) {
            "Cannot open input stream for URI: $imageUri"
        }
        val bytes = inputStream.use { it.readBytes() }
        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            name = "image",
            filename = "ocr_image.jpg",
            body = requestBody,
        )
        return ocrApi.extractIngredients(part)
    }
}
