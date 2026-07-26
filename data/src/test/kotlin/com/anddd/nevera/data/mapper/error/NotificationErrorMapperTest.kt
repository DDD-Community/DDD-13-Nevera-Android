package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.model.notification.GetNotificationTimeError
import com.anddd.nevera.domain.model.notification.UpdateNotificationEnabledError
import com.anddd.nevera.domain.model.notification.UpdateNotificationTimeError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * 알림 설정과 FCM 토큰 등록의 서버 에러 코드 매핑을 고정한다.
 *
 * 알림 시각 변경과 알림 on/off는 서버 코드 3001을 공유하면서도 각각 다른 도메인 에러로
 * 변환되므로, 두 함수를 따로 검증해 한쪽 변경이 다른 쪽으로 새지 않게 한다.
 */
class NotificationErrorMapperTest {

    @Test
    fun `알림 시각 조회 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(
            GetNotificationTimeError.MemberNotFound,
            httpError(2041).toGetNotificationTimeError(),
        )
    }

    @Test
    fun `알림 시각 조회 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            GetNotificationTimeError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toGetNotificationTimeError(),
        )
    }

    @Test
    fun `알림 사용 여부 변경 - 서버 코드 3001은 InvalidNotificationEnabled가 된다`() {
        assertEquals(
            UpdateNotificationEnabledError.InvalidNotificationEnabled,
            httpError(3001).toUpdateNotificationEnabledError(),
        )
    }

    @Test
    fun `알림 사용 여부 변경 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(
            UpdateNotificationEnabledError.MemberNotFound,
            httpError(2041).toUpdateNotificationEnabledError(),
        )
    }

    @Test
    fun `알림 사용 여부 변경 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            UpdateNotificationEnabledError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toUpdateNotificationEnabledError(),
        )
    }

    @Test
    fun `알림 시각 변경 - 서버 코드 3001은 InvalidNotificationTime이 된다`() {
        assertEquals(
            UpdateNotificationTimeError.InvalidNotificationTime,
            httpError(3001).toUpdateNotificationTimeError(),
        )
    }

    @Test
    fun `알림 시각 변경 - 서버 코드 4081은 InvalidNotificationMinute이 된다`() {
        assertEquals(
            UpdateNotificationTimeError.InvalidNotificationMinute,
            httpError(4081).toUpdateNotificationTimeError(),
        )
    }

    @Test
    fun `알림 시각 변경 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(
            UpdateNotificationTimeError.MemberNotFound,
            httpError(2041).toUpdateNotificationTimeError(),
        )
    }

    @Test
    fun `알림 시각 변경 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            UpdateNotificationTimeError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toUpdateNotificationTimeError(),
        )
    }

    @Test
    fun `FCM 토큰 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(FcmTokenError.MemberNotFound, httpError(2041).toFcmTokenError())
    }

    @Test
    fun `FCM 토큰 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            FcmTokenError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toFcmTokenError(),
        )
    }

    @Test
    fun `FCM 토큰 - 네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            FcmTokenError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toFcmTokenError(),
        )
    }
}
