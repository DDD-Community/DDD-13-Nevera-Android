package com.anddd.nevera.core.network.di

import com.anddd.nevera.core.network.BuildConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * [AuthInterceptor] 내에서 token refresh API 호출에 사용하는 전용 네트워크 인스턴스 모음.
 * [AuthInterceptor]를 포함하지 않아 refresh 요청이 인터셉터를 재진입하는 데드락을 방지한다.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object AuthNetworkModule {

    private const val OKHTTP_TAG = "OkHttp"
    private const val TIMEOUT_SECONDS = 30L

    private val loggingInterceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Timber.tag(OKHTTP_TAG).d(message)
        }.apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    @RefreshOkHttpClient
    fun provideRefreshOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        @RefreshOkHttpClient okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}
