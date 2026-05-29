package com.anddd.nevera.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.anddd.nevera.core.database.entity.NotificationEntity
import com.anddd.nevera.data.mapper.toEntity
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class NotificationRemoteMediator @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource,
    private val localDataSource: NotificationLocalDataSource,
) : RemoteMediator<Int, NotificationEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NotificationEntity>,
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> state.pages.sumOf { it.data.size }
        }
        return try {
            val response = remoteDataSource.getNotifications(offset)
            val items = response.result ?: emptyList()
            localDataSource.insertAllIgnoring(items.map { it.toEntity() })
            MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
