package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.domain.model.notification.GetNotificationTimeError
import com.anddd.nevera.domain.model.notification.UpdateNotificationEnabledError
import com.anddd.nevera.domain.model.notification.UpdateNotificationTimeError

private object NotificationTimeErrorCode {
    const val MEMBER_NOT_FOUND = 2041
    const val INVALID_NOTIFICATION_ENABLED = 3001
    const val INVALID_NOTIFICATION_TIME = 3001
    const val INVALID_NOTIFICATION_MINUTE = 4081
}

internal fun NetworkError.toGetNotificationTimeError(): GetNotificationTimeError = when (this) {
    is NetworkError.HttpError -> when (code) {
        NotificationTimeErrorCode.MEMBER_NOT_FOUND -> GetNotificationTimeError.MemberNotFound
        else -> GetNotificationTimeError.Common(toCommonError())
    }
    else -> GetNotificationTimeError.Common(toCommonError())
}

internal fun NetworkError.toUpdateNotificationEnabledError(): UpdateNotificationEnabledError = when (this) {
    is NetworkError.HttpError -> when (code) {
        NotificationTimeErrorCode.INVALID_NOTIFICATION_ENABLED -> UpdateNotificationEnabledError.InvalidNotificationEnabled
        NotificationTimeErrorCode.MEMBER_NOT_FOUND -> UpdateNotificationEnabledError.MemberNotFound
        else -> UpdateNotificationEnabledError.Common(toCommonError())
    }
    else -> UpdateNotificationEnabledError.Common(toCommonError())
}

internal fun NetworkError.toUpdateNotificationTimeError(): UpdateNotificationTimeError = when (this) {
    is NetworkError.HttpError -> when (code) {
        NotificationTimeErrorCode.INVALID_NOTIFICATION_TIME -> UpdateNotificationTimeError.InvalidNotificationTime
        NotificationTimeErrorCode.INVALID_NOTIFICATION_MINUTE -> UpdateNotificationTimeError.InvalidNotificationMinute
        NotificationTimeErrorCode.MEMBER_NOT_FOUND -> UpdateNotificationTimeError.MemberNotFound
        else -> UpdateNotificationTimeError.Common(toCommonError())
    }
    else -> UpdateNotificationTimeError.Common(toCommonError())
}
