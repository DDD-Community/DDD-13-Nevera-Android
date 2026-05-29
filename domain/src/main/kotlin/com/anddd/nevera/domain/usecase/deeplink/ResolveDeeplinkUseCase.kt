package com.anddd.nevera.domain.usecase.deeplink

import com.anddd.nevera.domain.model.deeplink.DeeplinkAction
import javax.inject.Inject

class ResolveDeeplinkUseCase @Inject constructor() {
    operator fun invoke(deeplink: String): DeeplinkAction? = when {
        deeplink.startsWith("nevera://detail/") -> {
            val id = deeplink.removePrefix("nevera://detail/")
            if (id.isNotBlank()) DeeplinkAction.NavigateToIngredientDetail(id) else null
        }
        else -> null
    }
}
