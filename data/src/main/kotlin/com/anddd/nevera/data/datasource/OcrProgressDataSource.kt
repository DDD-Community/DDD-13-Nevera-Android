package com.anddd.nevera.data.datasource

import kotlinx.coroutines.flow.Flow

internal interface OcrProgressDataSource {
    fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse>
}
