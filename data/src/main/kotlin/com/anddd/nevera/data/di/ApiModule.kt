package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.di.RefreshApi
import com.anddd.nevera.core.network.di.RefreshRetrofit
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.api.HomeApi
import com.anddd.nevera.data.api.IngredientApi
import com.anddd.nevera.data.api.NotificationApi
import com.anddd.nevera.data.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    @RefreshApi
    fun provideRefreshAuthApi(@RefreshRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIngredientApi(retrofit: Retrofit): IngredientApi {
        return retrofit.create(IngredientApi::class.java)
    }

}
