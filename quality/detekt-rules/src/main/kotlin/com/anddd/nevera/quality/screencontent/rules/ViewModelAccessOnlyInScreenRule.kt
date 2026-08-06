package com.anddd.nevera.quality.screencontent.rules

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

class ViewModelAccessOnlyInScreenRule(config: Config) : Rule(config) {

    private val screenOnlyCallees = setOf(
        "hiltViewModel",
        "viewModel",
        "collectAsState",
        "collectAsStateWithLifecycle",
        "collectSideEffect",
        "collectAsLazyPagingItems",
    )

    override val issue = Issue(
        id = "ViewModelAccessOnlyInScreenRule",
        severity = Severity.Defect,
        description = "ViewModel 주입·상태 구독(hiltViewModel, collectAsState, collectSideEffect 등)은 " +
            "*Screen Composable에서만 허용된다.",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.text ?: return
        if (callee !in screenOnlyCallees) return

        val packageName = expression.containingKtFile.packageFqName.asString()
        if (!packageName.contains("feature")) return

        if (isInsideScreenOrPreview(expression)) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "$callee 호출은 *Screen Composable에서만 허용된다. " +
                    "Content/Component는 uiState와 콜백만 파라미터로 받아야 하며, " +
                    "ViewModel 구독은 Screen이 담당한다.",
            )
        )
    }

    // Preview는 flowOf(...).collectAsLazyPagingItems() 같은 가짜 데이터 생성이 필요하므로 예외
    private fun isInsideScreenOrPreview(expression: KtCallExpression): Boolean {
        var fn = expression.getParentOfType<KtNamedFunction>(strict = true)
        while (fn != null) {
            if (fn.name?.endsWith("Screen") == true) return true
            val isPreview = fn.annotationEntries
                .any { it.shortName?.asString() == "Preview" }
            if (isPreview) return true
            fn = fn.getParentOfType<KtNamedFunction>(strict = true)
        }
        return false
    }
}
