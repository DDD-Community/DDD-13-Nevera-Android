package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.notification.NotificationListResponse
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// TODO 실제 API 연동 시, 제거
internal class FakeNotificationRemoteDataSource @Inject constructor() : NotificationRemoteDataSource {

    override suspend fun getNotifications(offset: Int): ApiResponse<List<NotificationListResponse>> {
        val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val all = listOf(
            fake(1, "삼겹살(12,000)이 내일까지예요", now.minusMinutes(59).format(fmt), "nevera://detail/101"),
            fake(2, "당근(2,000)이 내일까지예요", now.minusHours(23).format(fmt), "nevera://detail/102"),
            fake(3, "돼지고기(15,000)이 05.31까지예요", now.minusDays(1).format(fmt), "nevera://detail/103"),
            fake(4, "계란(3,000)이 05.29까지예요", now.minusDays(30).format(fmt), "nevera://detail/104"),
        )
        return ApiResponse(
            result = if (offset >= all.size) emptyList() else all.drop(offset),
            error = null,
        )
    }

    private fun fake(id: Long, message: String, createdAt: String, deeplink: String) =
        NotificationListResponse(
            id = id,
            title = "유통기한",
            message = message,
            deeplink = deeplink,
            type = "default",
            createdAt = createdAt,
        )

}
