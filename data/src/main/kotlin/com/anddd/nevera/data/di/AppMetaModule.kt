package com.anddd.nevera.data.di

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.anddd.nevera.data.di.qualifier.VersionName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppMetaModule {

    @Provides
    @Singleton
    @VersionName
    fun provideVersionName(@ApplicationContext context: Context): String {
        return runCatching {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            info.versionName.orEmpty()
        }.getOrDefault("")
    }
}
