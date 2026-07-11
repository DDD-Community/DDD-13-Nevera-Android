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
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class ScreenDelegatesToContentRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ScreenDelegatesToContentRule",
        severity = Severity.Defect,
        description = "*Screen Composable은 반드시 같은 접두사의 *Content를 호출해 렌더링을 위임해야 한다.",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val name = function.name ?: return
        if (!name.endsWith("Screen")) return

        val hasComposableAnnotation = function.annotationEntries
            .any { it.shortName?.asString() == "Composable" }
        if (!hasComposableAnnotation) return

        val packageName = function.containingKtFile.packageFqName.asString()
        if (!packageName.contains("feature")) return

        // LoadingContent 같은 공용 컴포넌트 호출로 우회할 수 없도록 접두사 일치를 요구한다
        val expectedContentName = name.removeSuffix("Screen") + "Content"
        val callsContent = function.collectDescendantsOfType<KtCallExpression>()
            .any { it.calleeExpression?.text == expectedContentName }

        if (!callsContent) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(function),
                    "$name 은 $expectedContentName 를 호출해 렌더링을 위임해야 한다. " +
                        "Screen은 상태 구독·SideEffect 처리만 담당하고, " +
                        "레이아웃은 $expectedContentName 에 배치한다.",
                )
            )
        }
    }
}
