package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.user.OnboardingStatusError
import com.anddd.nevera.domain.model.user.ProfileError
import com.anddd.nevera.domain.model.user.UpdateNicknameError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** 프로필 조회·온보딩 상태·닉네임 변경의 서버 에러 코드 매핑을 고정한다. */
class UserErrorMapperTest {

    @Test
    fun `프로필 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(ProfileError.MemberNotFound, httpError(2041).toProfileError())
    }

    @Test
    fun `프로필 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            ProfileError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toProfileError(),
        )
    }

    @Test
    fun `프로필 - 타임아웃은 공통 에러로 감싼다`() {
        assertEquals(
            ProfileError.Common(CommonError.Timeout),
            NetworkError.TimeoutError().toProfileError(),
        )
    }

    @Test
    fun `온보딩 상태 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(OnboardingStatusError.MemberNotFound, httpError(2041).toOnboardingStatusError())
    }

    @Test
    fun `온보딩 상태 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            OnboardingStatusError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toOnboardingStatusError(),
        )
    }

    @Test
    fun `닉네임 변경 - 서버 코드 3001은 InvalidNickname이 된다`() {
        assertEquals(UpdateNicknameError.InvalidNickname, httpError(3001).toUpdateNicknameError())
    }

    @Test
    fun `닉네임 변경 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            UpdateNicknameError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toUpdateNicknameError(),
        )
    }

    @Test
    fun `닉네임 변경 - 네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            UpdateNicknameError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toUpdateNicknameError(),
        )
    }
}
