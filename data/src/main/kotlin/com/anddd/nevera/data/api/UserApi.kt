package com.anddd.nevera.data.api

import com.anddd.nevera.data.model.LoginRequest
import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.SnsLoginRequest
import com.anddd.nevera.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface UserApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/sns-login")
    suspend fun snsLogin(@Body request: SnsLoginRequest): LoginResponse

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserResponse
}
