package com.anddd.nevera.domain.model.notification

import com.anddd.nevera.domain.model.common.CommonError

sealed interface GetNotificationTimeError {
    data object MemberNotFound : GetNotificationTimeError
    data class Common(val error: CommonError) : GetNotificationTimeError
}

sealed interface UpdateNotificationTimeError {
    data object InvalidNotificationTime : UpdateNotificationTimeError
    data object InvalidNotificationMinute : UpdateNotificationTimeError
    data object MemberNotFound : UpdateNotificationTimeError
    data class Common(val error: CommonError) : UpdateNotificationTimeError
}
