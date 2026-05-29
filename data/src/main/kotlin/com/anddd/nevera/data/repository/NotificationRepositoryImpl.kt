package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.NotificationRemoteDataSource
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.AppNotification
import com.anddd.nevera.domain.repository.NotificationRepository
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : NotificationRepository {

    override suspend fun getNotifications(offset: Int): NeveraResult<List<AppNotification>, CommonError> =
        apiCall {
            notificationRemoteDataSource.getNotifications(offset)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toCommonError() },
        )

    override suspend fun markAsRead(id: String): NeveraResult<Unit, CommonError> {
        // TODO: PR2 - Room DB 연동 후 실제 읽음 처리 구현
        return NeveraResult.Success(Unit)
    }
}
