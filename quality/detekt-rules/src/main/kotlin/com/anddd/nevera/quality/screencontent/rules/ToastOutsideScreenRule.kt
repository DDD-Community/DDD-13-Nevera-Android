package com.anddd.nevera.quality.screencontent.rules

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

class ToastOutsideScreenRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ToastOutsideScreenRule",
        severity = Severity.Defect,
        description = "Toast는 *Screen Composable(SideEffect 처리부)에서만 띄울 수 있다. " +
            "Content/Component에서는 recomposition마다 반복 실행되는 버그가, " +
            "ViewModel에서는 플랫폼 의존이 생긴다.",
        debt = Debt.TEN_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != "makeText") return

        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        // android.widget.Toast.makeText(...) 같은 FQCN 호출로 우회할 수 없도록 접미사까지 매칭한다
        val receiverText = qualified.receiverExpression.text
        if (receiverText != "Toast" && !receiverText.endsWith(".Toast")) return

        val packageName = expression.containingKtFile.packageFqName.asString()
        if (!packageName.contains("feature")) return

        if (isInsideScreen(expression)) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "Toast.makeText 호출은 *Screen Composable에서만 허용된다. " +
                    "ViewModel에서는 SideEffect를 발행하고, " +
                    "Screen의 collectSideEffect에서 Toast를 띄운다.",
            )
        )
    }

    private fun isInsideScreen(expression: KtCallExpression): Boolean {
        var fn = expression.getParentOfType<KtNamedFunction>(strict = true)
        while (fn != null) {
            if (fn.name?.endsWith("Screen") == true) return true
            fn = fn.getParentOfType<KtNamedFunction>(strict = true)
        }
        return false
    }
}
