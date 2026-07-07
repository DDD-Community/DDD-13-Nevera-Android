package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtUserType

class ContentComposableParameterRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ContentComposableParameterRule",
        severity = Severity.Defect,
        description = "*Content Composable의 파라미터는 *UiState, 함수 타입, Modifier만 허용된다.",
        debt = Debt.TEN_MINS,
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val name = function.name ?: return
        if (!name.endsWith("Content")) return

        val hasComposableAnnotation = function.annotationEntries
            .any { it.shortName?.asString() == "Composable" }
        if (!hasComposableAnnotation) return

        for (param in function.valueParameters) {
            if (!isAllowedParameter(param)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(param),
                        "$name 의 파라미터 '${param.name}'은 허용되지 않는다. " +
                            "*Content Composable은 *UiState, 함수 타입(onIntent), Modifier만 받을 수 있다. " +
                            "local state 값은 Screen에서 관리해야 한다.",
                    )
                )
            }
        }
    }

    private fun isAllowedParameter(param: KtParameter): Boolean {
        val typeRef = param.typeReference ?: return true
        val typeElement = typeRef.typeElement

        return when {
            typeElement is KtFunctionType -> true
            typeElement is KtUserType &&
                typeElement.referencedName?.endsWith("UiState") == true -> true
            typeElement is KtUserType &&
                typeElement.referencedName == "Modifier" -> true
            else -> false
        }
    }
}
