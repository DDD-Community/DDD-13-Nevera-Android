package com.anddd.nevera.domain.testutil

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.auth.LogoutError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.AuthRepository

/**
 * [AuthRepository]의 테스트용 구현.
 *
 * 반환값은 생성자 인자로 바꾸고, 호출 사실은 리스트에 기록한다.
 * 이 마일스톤에서 쓰지 않는 메서드는 호출되면 원인이 드러나도록 예외를 던진다.
 */
class FakeAuthRepository(
    var emailLoginResult: NeveraResult<LoginResult, LoginError> =
        NeveraResult.Success(LoginResult(accessToken = "access", refreshToken = "refresh")),
    var googleLoginResult: NeveraResult<LoginResult, GoogleLoginError> =
        NeveraResult.Success(LoginResult(accessToken = "access", refreshToken = "refresh")),
) : AuthRepository {

    val emailLoginCalls = mutableListOf<Pair<String, String>>()
    val googleLoginCalls = mutableListOf<String>()

    override suspend fun loginWithEmail(
        email: String,
        password: String,
    ): NeveraResult<LoginResult, LoginError> {
        emailLoginCalls += email to password
        return emailLoginResult
    }

    override suspend fun loginWithGoogle(idToken: String): NeveraResult<LoginResult, GoogleLoginError> {
        googleLoginCalls += idToken
        return googleLoginResult
    }

    override suspend fun signup(
        email: String,
        password: String,
    ): NeveraResult<MessageResult, SignupError> = notStubbed("signup")

    override suspend fun emailRequest(email: String): NeveraResult<MessageResult, EmailRequestError> =
        notStubbed("emailRequest")

    override suspend fun emailVerify(
        email: String,
        authCode: String,
    ): NeveraResult<MessageResult, EmailVerifyError> = notStubbed("emailVerify")

    override suspend fun logout(): NeveraResult<MessageResult, LogoutError> = notStubbed("logout")

    override suspend fun withdraw(): NeveraResult<MessageResult, WithdrawError> = notStubbed("withdraw")

    private fun notStubbed(name: String): Nothing =
        throw UnsupportedOperationException("FakeAuthRepository.$name 은 이 테스트에서 준비되지 않았다")
}
