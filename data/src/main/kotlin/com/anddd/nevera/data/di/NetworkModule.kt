package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.di.AuthInterceptorQualifier
import com.anddd.nevera.core.network.di.AuthOkHttpClient
import com.anddd.nevera.core.network.di.AuthRetrofit
import com.anddd.nevera.data.BuildConfig
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.api.DbTestApi
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.auth.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val BASE_URL = "https://api.nevera.n-e.kr/"
    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideDbTestApi(retrofit: Retrofit): DbTestApi =
        retrofit.create(DbTestApi::class.java)

    // AuthInterceptor를 포함하지 않는 별도 OkHttpClient.
    // refresh token 갱신 요청에 AuthInterceptor가 끼면 401 → refresh → 401 → refresh 의 무한 루프가 발생하므로,
    // auth 관련 API 전용으로 인터셉터 없는 클라이언트를 분리한다.
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                            else HttpLoggingInterceptor.Level.NONE
                }
            )
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(@AuthOkHttpClient client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(@AuthRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @AuthInterceptorQualifier
    fun provideAuthInterceptor(interceptor: AuthInterceptor): Interceptor = interceptor
}