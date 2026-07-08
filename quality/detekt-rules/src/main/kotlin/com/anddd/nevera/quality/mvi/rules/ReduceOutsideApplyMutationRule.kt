package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
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

        // list.reduce {} 같은 리시버 있는 호출은 MVI reduce가 아니므로 제외
        if (expression.parent is KtDotQualifiedExpression) return

        if (!isInsideApplyMutation(expression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "reduce { }는 applyMutation() 내부에서만 호출할 수 있다.",
                )
            )
        }
    }

    private fun isInsideApplyMutation(expression: KtCallExpression): Boolean {
        var fn = expression.getParentOfType<KtNamedFunction>(strict = true)
        while (fn != null) {
            if (fn.name == "applyMutation") return true
            fn = fn.getParentOfType<KtNamedFunction>(strict = true)
        }
        return false
    }
}
