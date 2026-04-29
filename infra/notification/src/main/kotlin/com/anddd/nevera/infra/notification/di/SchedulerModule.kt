package com.anddd.nevera.infra.notification.di

import com.anddd.nevera.infra.notification.scheduler.WorkManagerFcmSyncScheduler
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract fun bindFcmSyncScheduler(impl: WorkManagerFcmSyncScheduler): FcmSyncScheduler
}