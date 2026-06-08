package com.anddd.nevera.domain.model.notification

import com.anddd.nevera.domain.model.common.CommonError

sealed interface UpdateNotificationEnabledError {
    data object InvalidNotificationEnabled : UpdateNotificationEnabledError
    data object MemberNotFound : UpdateNotificationEnabledError
    data class Common(val error: CommonError) : UpdateNotificationEnabledError
}
