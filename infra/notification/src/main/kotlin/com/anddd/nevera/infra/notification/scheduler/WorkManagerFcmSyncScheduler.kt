package com.anddd.nevera.infra.notification.scheduler

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.anddd.nevera.infra.notification.worker.FcmTokenSyncWorker
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkManagerFcmSyncScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : FcmSyncScheduler {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    override fun scheduleUpdateFcmToken(newFcmToken: String) {
        workManager.enqueueUniqueWork(
            FcmTokenSyncWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            FcmTokenSyncWorker.createUpdateRequest(newFcmToken),
        )
    }

    override fun scheduleSyncFcmToken() {
        workManager.enqueueUniqueWork(
            FcmTokenSyncWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            FcmTokenSyncWorker.createSyncRequest(),
        )
    }

    override fun cancelSyncFcmToken() {
        workManager.cancelUniqueWork(FcmTokenSyncWorker.WORK_NAME)
    }
}
