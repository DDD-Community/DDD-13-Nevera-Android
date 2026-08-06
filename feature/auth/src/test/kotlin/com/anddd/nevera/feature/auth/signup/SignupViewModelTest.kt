package com.anddd.nevera.feature.auth.signup

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.domain.usecase.auth.SignupUseCase
import com.anddd.nevera.domain.usecase.email.EmailRequestUseCase
import com.anddd.nevera.domain.usecase.email.EmailVerifyUseCase
import com.anddd.nevera.domain.usecase.validation.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.validation.ValidatePasswordUseCase
import com.anddd.nevera.feature.auth.signup.model.AuthCodeSectionError
import com.anddd.nevera.feature.auth.signup.model.SignupIntent
import com.anddd.nevera.feature.auth.signup.model.SignupSideEffect
import com.anddd.nevera.feature.auth.signup.model.SignupUiState
import com.anddd.nevera.feature.auth.signup.model.withAuthCodeDescription
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.orbitmvi.orbit.test.test

/**
 * 회원가입 화면의 입력 검증과 서버 응답 처리 계약을 고정한다.
 *
 * `orbit-test`의 `test()`는 검증용 컨테이너를 새로 만들어 바꿔치기하므로 `init` 블록에서
 * 시작되는 타이머 구독은 관찰되지 않는다. 여기서는 `initialState`로 상태를 주입하고
 * 사용자 액션이 만드는 상태 전이와 SideEffect만 검증한다.
 *
 * 검증용 UseCase(`ValidateEmailUseCase`, `ValidatePasswordUseCase`)는 의존성이 없는 순수
 * 함수이므로 대역 대신 실제 구현을 쓴다. 그래야 "이 비밀번호가 실제로 통과하는가"까지
 * 함께 검증된다. 서버를 호출하는 세 UseCase만 대역으로 바꾼다.
 */
class SignupViewModelTest {

    private val emailRequestUseCase = mockk<EmailRequestUseCase>()
    private val emailVerifyUseCase = mockk<EmailVerifyUseCase>()
    private val signupUseCase = mockk<SignupUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SignupViewModel(
        emailRequestUseCase = emailRequestUseCase,
        emailVerifyUseCase = emailVerifyUseCase,
        signupUseCase = signupUseCase,
        validateEmailUseCase = ValidateEmailUseCase(),
        validatePasswordUseCase = ValidatePasswordUseCase(),
    )

    /** 이메일 인증까지 마치고 모든 입력이 올바른, 가입 버튼을 누를 수 있는 상태. */
    private val readyToSignup = SignupUiState(
        email = "user@example.com",
        password = "password1!",
        confirmPassword = "password1!",
        emailValidation = EmailValidationResult.Valid,
        passwordValidation = PasswordValidationResult.Valid,
        isPasswordMatched = true,
        isEmailRequestSent = true,
        isEmailVerified = true,
    ).withAuthCodeDescription()

    // ── 가입 요청 전 방어 ────────────────────────────────────────────────────────

    @Test
    fun `이메일 인증을 마치지 않았으면 가입을 요청하지 않는다`() = runTest {
        val state = readyToSignup.copy(isEmailVerified = false).withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectSideEffect(SignupSideEffect.SignupEmailNotVerified)
        }

        coVerify(exactly = 0) { signupUseCase(any(), any()) }
    }

    @Test
    fun `비밀번호가 조건을 만족하지 않으면 가입을 요청하지 않는다`() = runTest {
        val state = readyToSignup.copy(password = "짧음", confirmPassword = "짧음").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState {
                copy(
                    emailValidation = EmailValidationResult.Valid,
                    passwordValidation = ValidatePasswordUseCase()("짧음"),
                    isPasswordMatched = true,
                )
            }
        }

        coVerify(exactly = 0) { signupUseCase(any(), any()) }
    }

    @Test
    fun `비밀번호 확인이 일치하지 않으면 가입을 요청하지 않는다`() = runTest {
        val state = readyToSignup.copy(confirmPassword = "다른비밀번호1!").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState { copy(isPasswordMatched = false) }
        }

        coVerify(exactly = 0) { signupUseCase(any(), any()) }
    }

    @Test
    fun `비밀번호 확인이 비어 있으면 가입을 요청하지 않는다`() = runTest {
        val state = readyToSignup.copy(confirmPassword = "", isPasswordMatched = false).withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.Signup)

            // 검증 실패가 만드는 상태는 이미 화면에 반영된 값과 같아 새 방출이 없다.
            // 관찰 가능한 결과는 "가입 요청이 나가지 않는다"뿐이다.
            expectNoItems()
        }

        coVerify(exactly = 0) { signupUseCase(any(), any()) }
    }

    // ── 가입 요청 결과 ───────────────────────────────────────────────────────────

    @Test
    fun `가입에 성공하면 로그인 화면으로 이동한다`() = runTest {
        coEvery { signupUseCase("user@example.com", "password1!") } returns
            NeveraResult.Success(MessageResult("가입 완료"))

        createViewModel().test(this, initialState = readyToSignup) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState { copy(isLoading = true) }
            expectSideEffect(SignupSideEffect.MoveToLoginScreen)
        }
    }

    @Test
    fun `가입이 미인증 이메일로 거절되면 해당 안내를 띄우고 로딩을 끝낸다`() = runTest {
        coEvery { signupUseCase(any(), any()) } returns
            NeveraResult.Failure(SignupError.UnverifiedEmail("이메일 인증이 필요합니다"))

        createViewModel().test(this, initialState = readyToSignup) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
            expectSideEffect(SignupSideEffect.SignupUnverifiedEmail("이메일 인증이 필요합니다"))
        }
    }

    @Test
    fun `가입이 인증 내역 없음으로 거절되면 해당 안내를 띄운다`() = runTest {
        coEvery { signupUseCase(any(), any()) } returns
            NeveraResult.Failure(SignupError.NotFound("인증 내역이 없습니다"))

        createViewModel().test(this, initialState = readyToSignup) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
            expectSideEffect(SignupSideEffect.SignupAuthNotFound("인증 내역이 없습니다"))
        }
    }

    @Test
    fun `가입이 공통 에러로 실패하면 서버 오류 안내를 띄운다`() = runTest {
        coEvery { signupUseCase(any(), any()) } returns
            NeveraResult.Failure(SignupError.Common(CommonError.NetworkUnavailable))

        createViewModel().test(this, initialState = readyToSignup) {
            containerHost.handleIntent(SignupIntent.Signup)

            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
            expectSideEffect(SignupSideEffect.SignupServerError)
        }
    }

    // ── 인증번호 확인 ────────────────────────────────────────────────────────────

    @Test
    fun `인증번호가 비어 있으면 확인 요청을 보내지 않는다`() = runTest {
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "  ").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectNoItems()
        }

        coVerify(exactly = 0) { emailVerifyUseCase(any(), any()) }
    }

    @Test
    fun `인증번호 확인에 성공하면 인증 완료 상태가 된다`() = runTest {
        coEvery { emailVerifyUseCase("user@example.com", "123456") } returns
            NeveraResult.Success(MessageResult("인증 완료"))
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "123456").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectState { copy(isLoading = true) }
            expectState {
                copy(
                    isLoading = false,
                    isEmailVerified = true,
                    authCodeSectionError = AuthCodeSectionError.None,
                ).withAuthCodeDescription()
            }
        }
    }

    @Test
    fun `인증번호가 틀리면 불일치 오류를 표시한다`() = runTest {
        coEvery { emailVerifyUseCase(any(), any()) } returns
            NeveraResult.Failure(EmailVerifyError.InvalidCode("인증번호가 올바르지 않습니다"))
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "000000").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.InvalidCode)
                    .withAuthCodeDescription()
            }
        }
    }

    @Test
    fun `서버에서 인증 시간이 만료되면 만료 오류를 표시한다`() = runTest {
        coEvery { emailVerifyUseCase(any(), any()) } returns
            NeveraResult.Failure(EmailVerifyError.ExpiredCode("만료되었습니다"))
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "000000").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.ServerExpired)
                    .withAuthCodeDescription()
            }
        }
    }

    @Test
    fun `인증 요청 내역이 없으면 오류 표시와 함께 안내도 띄운다`() = runTest {
        coEvery { emailVerifyUseCase(any(), any()) } returns
            NeveraResult.Failure(EmailVerifyError.NotFound("인증 요청 내역이 없습니다"))
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "000000").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.NotFound)
                    .withAuthCodeDescription()
            }
            expectSideEffect(SignupSideEffect.EmailVerifyNotFound("인증 요청 내역이 없습니다"))
        }
    }

    @Test
    fun `인증 확인이 공통 에러로 실패하면 불일치 오류로 축약해 표시한다`() = runTest {
        coEvery { emailVerifyUseCase(any(), any()) } returns
            NeveraResult.Failure(EmailVerifyError.Common(CommonError.Timeout))
        val state = readyToSignup.copy(isEmailVerified = false, authCode = "000000").withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.VerifyAuthCode)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.InvalidCode)
                    .withAuthCodeDescription()
            }
        }
    }

    // ── 인증번호 발송 ────────────────────────────────────────────────────────────

    @Test
    fun `이메일 형식이 올바르지 않으면 발송 요청을 보내지 않는다`() = runTest {
        val state = SignupUiState(email = "형식이아님")

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.RequestEmailVerification)

            expectState {
                copy(emailValidation = EmailValidationResult.InvalidFormat).withAuthCodeDescription()
            }
        }

        coVerify(exactly = 0) { emailRequestUseCase(any()) }
    }

    @Test
    fun `첫 발송에서 이미 가입된 이메일이면 섹션 오류 없이 안내만 띄운다`() = runTest {
        coEvery { emailRequestUseCase("user@example.com") } returns
            NeveraResult.Failure(EmailRequestError.DuplicateEmail("이미 가입된 이메일입니다"))
        val state = SignupUiState(
            email = "user@example.com",
            emailValidation = EmailValidationResult.Valid,
            isEmailRequestSent = false,
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.RequestEmailVerification)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.None)
                    .withAuthCodeDescription()
            }
            expectSideEffect(SignupSideEffect.EmailRequestDuplicateEmail("이미 가입된 이메일입니다"))
        }
    }

    @Test
    fun `재발송에서 이미 가입된 이메일이면 인증 섹션에도 오류를 표시한다`() = runTest {
        // 재발송 시점에는 인증코드 입력란이 이미 열려 있으므로 그쪽에도 사유를 보여줘야 한다.
        coEvery { emailRequestUseCase("user@example.com") } returns
            NeveraResult.Failure(EmailRequestError.DuplicateEmail("이미 가입된 이메일입니다"))
        val state = SignupUiState(
            email = "user@example.com",
            emailValidation = EmailValidationResult.Valid,
            isEmailRequestSent = true,
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.RequestEmailVerification)

            expectState { copy(isLoading = true) }
            expectState {
                copy(
                    isLoading = false,
                    authCodeSectionError = AuthCodeSectionError.EmailAlreadyRegistered,
                ).withAuthCodeDescription()
            }
            expectSideEffect(SignupSideEffect.EmailRequestDuplicateEmail("이미 가입된 이메일입니다"))
        }
    }

    @Test
    fun `메일 발송에 실패하면 발송 실패 안내를 띄운다`() = runTest {
        coEvery { emailRequestUseCase(any()) } returns
            NeveraResult.Failure(EmailRequestError.MailSendError("메일 발송에 실패했습니다"))
        val state = SignupUiState(
            email = "user@example.com",
            emailValidation = EmailValidationResult.Valid,
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.RequestEmailVerification)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.None)
                    .withAuthCodeDescription()
            }
            expectSideEffect(SignupSideEffect.EmailRequestMailSendError("메일 발송에 실패했습니다"))
        }
    }

    @Test
    fun `발송이 공통 에러로 실패하면 네트워크 안내를 띄운다`() = runTest {
        coEvery { emailRequestUseCase(any()) } returns
            NeveraResult.Failure(EmailRequestError.Common(CommonError.NetworkUnavailable))
        val state = SignupUiState(
            email = "user@example.com",
            emailValidation = EmailValidationResult.Valid,
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.RequestEmailVerification)

            expectState { copy(isLoading = true) }
            expectState {
                copy(isLoading = false, authCodeSectionError = AuthCodeSectionError.None)
                    .withAuthCodeDescription()
            }
            expectSideEffect(SignupSideEffect.EmailRequestNetworkError(null))
        }
    }

    // ── 입력값 반영 ──────────────────────────────────────────────────────────────

    @Test
    fun `이메일을 바꾸면 인증 진행 상태가 모두 초기화된다`() = runTest {
        createViewModel().test(this, initialState = readyToSignup) {
            containerHost.handleIntent(SignupIntent.EmailChanged("other@example.com"))

            expectState {
                copy(
                    email = "other@example.com",
                    emailValidation = EmailValidationResult.Valid,
                    authCode = "",
                    isEmailRequestSent = false,
                    isEmailVerified = false,
                    authCodeSectionError = AuthCodeSectionError.None,
                ).withAuthCodeDescription()
            }
        }
    }

    @Test
    fun `비밀번호를 확인란과 같게 입력하면 일치로 표시된다`() = runTest {
        val state = SignupUiState(confirmPassword = "password1!")

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.PasswordChanged("password1!"))

            expectState {
                copy(
                    password = "password1!",
                    passwordValidation = PasswordValidationResult.Valid,
                    isPasswordMatched = true,
                )
            }
        }
    }

    @Test
    fun `확인란을 비밀번호와 다르게 입력하면 불일치로 표시된다`() = runTest {
        val state = SignupUiState(password = "password1!")

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.ConfirmPasswordChanged("different1!"))

            expectState { copy(confirmPassword = "different1!", isPasswordMatched = false) }
        }
    }

    @Test
    fun `인증번호를 새로 입력하면 이전 인증 실패 표시를 지운다`() = runTest {
        val state = readyToSignup.copy(
            isEmailVerified = false,
            authCode = "000000",
            authCodeSectionError = AuthCodeSectionError.InvalidCode,
        ).withAuthCodeDescription()

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(SignupIntent.AuthCodeChanged("123456"))

            expectState {
                copy(authCode = "123456", authCodeSectionError = AuthCodeSectionError.None)
                    .withAuthCodeDescription()
            }
        }
    }

    @Test
    fun `뒤로가기를 누르면 로그인 화면으로 이동한다`() = runTest {
        createViewModel().test(this, initialState = SignupUiState()) {
            containerHost.handleIntent(SignupIntent.NavigateBack)

            expectSideEffect(SignupSideEffect.MoveToLoginScreen)
        }
    }
}
