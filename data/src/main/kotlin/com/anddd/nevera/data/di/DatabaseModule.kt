package com.anddd.nevera.data.di

import android.content.Context
import androidx.room.Room
import com.anddd.nevera.core.database.AppDatabase
import com.anddd.nevera.core.database.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "nevera-db",
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideNotificationDao(db: AppDatabase): NotificationDao =
        db.notificationDao()
}
