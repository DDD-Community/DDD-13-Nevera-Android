package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class ReduceOutsideApplyMutationRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ReduceOutsideApplyMutationRule",
        severity = Severity.Defect,
        description = "reduce { }는 applyMutation() 내부에서만 호출해야 한다.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.text ?: return
        if (callee != "reduce") return

        val enclosingFunction = expression.getParentOfType<KtNamedFunction>(strict = true)
        if (enclosingFunction?.name != "applyMutation") {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "reduce { }는 applyMutation() 내부에서만 호출할 수 있다. " +
                        "현재 위치: ${enclosingFunction?.name ?: "알 수 없는 함수"}",
                )
            )
        }
    }
}
