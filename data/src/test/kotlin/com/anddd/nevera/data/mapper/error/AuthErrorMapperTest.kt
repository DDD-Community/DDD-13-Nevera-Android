package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LogoutError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.CommonError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * 인증 관련 서버 에러 코드가 도메인 에러로 변환되는 규칙을 고정한다.
 *
 * 이 매핑은 서버 스펙이 코드에 숫자로 박혀 있는 지점이라, 서버가 코드 체계를 바꾸면
 * 앱은 조용히 잘못된 에러 메시지를 띄우고 컴파일은 그대로 통과한다.
 */
class AuthErrorMapperTest {

    @Test
    fun `로그인 - 서버 코드 2008은 InvalidCredentials가 된다`() {
        assertEquals(LoginError.InvalidCredentials, httpError(2008).toLoginError())
    }

    @Test
    fun `로그인 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            LoginError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toLoginError(),
        )
    }

    @Test
    fun `로그인 - 네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            LoginError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toLoginError(),
        )
    }

    @Test
    fun `회원가입 - 서버 코드 2003은 UnverifiedEmail이 되고 서버 메시지를 그대로 전달한다`() {
        assertEquals(
            SignupError.UnverifiedEmail("이메일 인증이 필요합니다"),
            httpError(2003, "이메일 인증이 필요합니다").toSignupError(),
        )
    }

    @Test
    fun `회원가입 - 서버 코드 2005는 NotFound가 된다`() {
        assertEquals(SignupError.NotFound("서버 메시지"), httpError(2005).toSignupError())
    }

    @Test
    fun `회원가입 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            SignupError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toSignupError(),
        )
    }

    @Test
    fun `구글 로그인 - 서버 코드 2011은 InvalidToken이 된다`() {
        assertEquals(
            GoogleLoginError.InvalidToken("유효하지 않은 토큰"),
            httpError(2011, "유효하지 않은 토큰").toGoogleLoginError(),
        )
    }

    @Test
    fun `구글 로그인 - 타임아웃은 공통 에러로 감싼다`() {
        assertEquals(
            GoogleLoginError.Common(CommonError.Timeout),
            NetworkError.TimeoutError().toGoogleLoginError(),
        )
    }

    @Test
    fun `이메일 인증 요청 - 서버 코드 2006은 DuplicateEmail이 된다`() {
        assertEquals(
            EmailRequestError.DuplicateEmail("이미 가입된 이메일입니다"),
            httpError(2006, "이미 가입된 이메일입니다").toEmailRequestError(),
        )
    }

    @Test
    fun `이메일 인증 요청 - 서버 코드 2007은 MailSendError가 된다`() {
        assertEquals(EmailRequestError.MailSendError("서버 메시지"), httpError(2007).toEmailRequestError())
    }

    @Test
    fun `이메일 인증 요청 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            EmailRequestError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toEmailRequestError(),
        )
    }

    @Test
    fun `이메일 인증 확인 - 서버 코드 2001은 InvalidCode가 된다`() {
        assertEquals(EmailVerifyError.InvalidCode("서버 메시지"), httpError(2001).toEmailVerifyError())
    }

    @Test
    fun `이메일 인증 확인 - 서버 코드 2002는 ExpiredCode가 된다`() {
        assertEquals(EmailVerifyError.ExpiredCode("서버 메시지"), httpError(2002).toEmailVerifyError())
    }

    @Test
    fun `이메일 인증 확인 - 서버 코드 2005는 NotFound가 된다`() {
        assertEquals(EmailVerifyError.NotFound("서버 메시지"), httpError(2005).toEmailVerifyError())
    }

    @Test
    fun `이메일 인증 확인 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            EmailVerifyError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toEmailVerifyError(),
        )
    }

    @Test
    fun `로그아웃 - 서버 코드 2023은 TokenNotFound가 된다`() {
        assertEquals(LogoutError.TokenNotFound("서버 메시지"), httpError(2023).toLogoutError())
    }

    @Test
    fun `로그아웃 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            LogoutError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toLogoutError(),
        )
    }

    @Test
    fun `회원탈퇴 - HTTP 401은 SessionInvalid가 된다`() {
        assertEquals(WithdrawError.SessionInvalid, httpError(401).toWithdrawError())
    }

    @Test
    fun `회원탈퇴 - HTTP 404는 SessionInvalid가 된다`() {
        assertEquals(WithdrawError.SessionInvalid, httpError(404).toWithdrawError())
    }

    @Test
    fun `회원탈퇴 - 그 밖의 코드는 공통 에러로 감싼다`() {
        assertEquals(
            WithdrawError.Common(CommonError.ServerError("서버 메시지")),
            httpError(500).toWithdrawError(),
        )
    }
}
