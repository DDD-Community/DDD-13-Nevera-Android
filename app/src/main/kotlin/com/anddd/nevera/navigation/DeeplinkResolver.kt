package com.anddd.nevera.navigation

import androidx.navigation3.runtime.NavKey
import com.anddd.nevera.feature.fridge.api.EditFridgeIngredientRoute
import com.anddd.nevera.feature.fridge.api.FridgeRoute
import java.net.URI
import javax.inject.Inject

/**
 * 딥링크 URI를 화면 스택으로 해석한다.
 *
 * domain이 아니라 :app이 소유한다. URL 스킴 해석은 비즈니스 규칙이 아니라
 * 조립 지점의 관심사이고, 목적지 이름을 아는 것도 조립 지점뿐이다.
 *
 * @property tab 어느 탭 위에 열 것인가
 * @property stack 탭 루트 위에 쌓을 화면들
 */
data class DeeplinkTarget(
    val tab: NavKey,
    val stack: List<NavKey>,
)

class DeeplinkResolver @Inject constructor() {

    fun resolve(deeplink: String): DeeplinkTarget? {
        // android.net.Uri를 쓰지 않는다. 그 타입은 순수 JVM 단위 테스트에서
        // 동작하지 않아 딥링크 파싱을 검증할 수 없게 만든다.
        val uri = runCatching { URI(deeplink.trim()) }.getOrNull() ?: return null
        if (uri.scheme != SCHEME) return null

        return when (uri.host) {
            HOST_DETAIL -> uri.lastPathSegment()
                ?.toLongOrNull()
                ?.let { id ->
                    DeeplinkTarget(
                        tab = FridgeRoute,
                        stack = listOf(EditFridgeIngredientRoute(id)),
                    )
                }

            else -> null
        }
    }

    private fun URI.lastPathSegment(): String? =
        path?.split("/")?.lastOrNull { it.isNotBlank() }

    private companion object {
        const val SCHEME = "nevera"
        const val HOST_DETAIL = "detail"
    }
}
