package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.home.HomeSummaryResponse
import retrofit2.http.GET

internal interface HomeApi {

    @GET("api/v1/savings/home")
    suspend fun getSummary(): ApiResponse<HomeSummaryResponse>
}
