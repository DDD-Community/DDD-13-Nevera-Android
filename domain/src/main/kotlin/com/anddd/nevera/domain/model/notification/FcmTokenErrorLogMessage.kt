package com.anddd.nevera.domain.model.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.domain.model.common.CommonError

fun FcmTokenError.toLogMessage(): String = when (this) {
    FcmTokenError.MemberNotFound -> "FCM 동기화 실패: MEMBER_NOT_FOUND(status=404, code=2041, message=존재하지 않는 사용자입니다.)"
    is FcmTokenError.Common -> when (val commonError = error) {
        CommonError.NetworkUnavailable -> "FCM 동기화 실패: 네트워크 연결을 확인해주세요."
        CommonError.Timeout -> "FCM 동기화 실패: 요청 시간이 초과되었습니다."
        CommonError.Unauthorized -> "FCM 동기화 실패: 인증되지 않은 요청입니다."
        is CommonError.ServerError -> "FCM 동기화 실패: 서버 오류(${commonError.message})"
        CommonError.Unknown -> "FCM 동기화 실패: 알 수 없는 오류가 발생했습니다."
    }
}

inline fun NeveraResult<Unit, FcmTokenError>.logFcmSyncFailure(
    tag: String,
    logWarning: (String, String) -> Unit,
): NeveraResult<Unit, FcmTokenError> {
    return onFailure { error ->
        logWarning(tag, error.toLogMessage())
    }
}
