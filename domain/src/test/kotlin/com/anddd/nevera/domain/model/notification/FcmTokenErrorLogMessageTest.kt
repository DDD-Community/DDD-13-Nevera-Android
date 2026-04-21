package com.anddd.nevera.domain.model.notification

import com.anddd.nevera.domain.model.common.CommonError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FcmTokenErrorLogMessageTest {

    @Test
    fun `member not found 에러를 서버 코드 포함 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: MEMBER_NOT_FOUND(status=404, code=2041, message=존재하지 않는 사용자입니다.)",
            FcmTokenError.MemberNotFound.toLogMessage(),
        )
    }

    @Test
    fun `server error 를 서버 오류 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: 서버 오류(boom)",
            FcmTokenError.Common(CommonError.ServerError("boom")).toLogMessage(),
        )
    }

    @Test
    fun `network unavailable 에러를 네트워크 오류 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: 네트워크 연결을 확인해주세요.",
            FcmTokenError.Common(CommonError.NetworkUnavailable).toLogMessage(),
        )
    }

    @Test
    fun `timeout 에러를 시간 초과 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: 요청 시간이 초과되었습니다.",
            FcmTokenError.Common(CommonError.Timeout).toLogMessage(),
        )
    }

    @Test
    fun `unauthorized 에러를 인증 오류 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: 인증되지 않은 요청입니다.",
            FcmTokenError.Common(CommonError.Unauthorized).toLogMessage(),
        )
    }

    @Test
    fun `unknown 에러를 알 수 없는 오류 메시지로 변환한다`() {
        assertEquals(
            "FCM 동기화 실패: 알 수 없는 오류가 발생했습니다.",
            FcmTokenError.Common(CommonError.Unknown).toLogMessage(),
        )
    }
}
