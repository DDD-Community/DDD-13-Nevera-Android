package com.anddd.nevera.feature.mypage.appinfo

import com.anddd.nevera.domain.usecase.appinfo.GetAppInfoUseCase
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoIntent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoSideEffect
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoUiModel
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoUiState
import com.anddd.nevera.feature.mypage.testutil.FakeAppInfoRepository
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
 * 앱 정보 화면의 Intent → SideEffect 계약을 고정한다.
 *
 * ## orbit-test와 이 프로젝트의 초기화 방식
 *
 * `orbit-test`의 `test()`는 검증용 컨테이너를 새로 만들어 기존 컨테이너와 바꿔치기한다.
 * 그래서 ViewModel 생성자(`init` 블록)에서 시작된 작업은 바꿔치기 이전의 컨테이너로 가고,
 * 테스트가 관찰하는 새 컨테이너에서는 재현되지 않는다. `AppInfoViewModel`은 `init`에서
 * 앱 정보를 불러오므로, 그 로딩 과정 자체는 이 방식으로 검증할 수 없다.
 *
 * 대신 `test(initialState = ...)`로 "이미 로딩이 끝난 상태"를 직접 주입하고,
 * 그 상태에서 사용자 액션이 올바른 SideEffect를 내는지를 검증한다.
 * 로딩 로직 자체는 [GetAppInfoUseCase]와 그 아래 저장소 테스트가 담당한다.
 */
class AppInfoViewModelTest {

    private val repository = FakeAppInfoRepository()

    @BeforeEach
    fun setUp() {
        // ViewModel의 viewModelScope는 Dispatchers.Main을 요구한다.
        // 테스트 컨테이너는 별도 스코프에서 돌지만, ViewModel 생성 자체가 Main을 건드린다.
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AppInfoViewModel(GetAppInfoUseCase(repository))

    private val loadedState = AppInfoUiState(
        isLoading = false,
        appInfo = AppInfoUiModel(
            termsUrl = "https://nevera.example.com/terms",
            privacyPolicyUrl = "https://nevera.example.com/privacy",
            versionName = "V1.2.3",
        ),
    )

    @Test
    fun `뒤로가기를 누르면 뒤로가기 SideEffect가 발생한다`() = runTest {
        createViewModel().test(this, initialState = loadedState) {
            containerHost.handleIntent(AppInfoIntent.NavigateBack)

            expectSideEffect(AppInfoSideEffect.NavigateBack)
        }
    }

    @Test
    fun `약관을 누르면 불러온 약관 주소를 여는 SideEffect가 발생한다`() = runTest {
        createViewModel().test(this, initialState = loadedState) {
            containerHost.handleIntent(AppInfoIntent.TermsClicked)

            expectSideEffect(AppInfoSideEffect.OpenUrl(loadedState.appInfo.termsUrl))
        }
    }

    @Test
    fun `개인정보 처리방침을 누르면 해당 주소를 여는 SideEffect가 발생한다`() = runTest {
        createViewModel().test(this, initialState = loadedState) {
            containerHost.handleIntent(AppInfoIntent.PrivacyPolicyClicked)

            expectSideEffect(AppInfoSideEffect.OpenUrl(loadedState.appInfo.privacyPolicyUrl))
        }
    }

    @Test
    fun `약관과 개인정보 처리방침은 서로 다른 주소를 연다`() = runTest {
        // 두 SideEffect가 같은 타입(OpenUrl)이라 매핑이 뒤바뀌어도 컴파일은 통과한다.
        createViewModel().test(this, initialState = loadedState) {
            containerHost.handleIntent(AppInfoIntent.TermsClicked)
            expectSideEffect(AppInfoSideEffect.OpenUrl(loadedState.appInfo.termsUrl))

            containerHost.handleIntent(AppInfoIntent.PrivacyPolicyClicked)
            expectSideEffect(AppInfoSideEffect.OpenUrl(loadedState.appInfo.privacyPolicyUrl))
        }
    }

    @Test
    fun `주소를 아직 불러오지 못한 상태에서 약관을 누르면 빈 주소를 연다`() = runTest {
        // 현재 동작이다. 로딩 실패 시 빈 URL이 웹뷰로 전달되므로 화면에서 처리가 필요하다.
        createViewModel().test(this, initialState = AppInfoUiState()) {
            containerHost.handleIntent(AppInfoIntent.TermsClicked)

            expectSideEffect(AppInfoSideEffect.OpenUrl(""))
        }
    }
}
