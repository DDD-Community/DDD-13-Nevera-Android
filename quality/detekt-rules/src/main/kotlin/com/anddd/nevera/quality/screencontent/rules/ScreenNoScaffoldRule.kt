package com.anddd.nevera.quality.screencontent.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class ScreenNoScaffoldRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ScreenNoScaffoldRule",
        severity = Severity.Defect,
        description = "*Screen 파일에서 Scaffold를 직접 호출할 수 없다. 레이아웃 뼈대는 *Content가 담당한다.",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != "Scaffold") return

        // 파일 단위로 검사한다 — Screen 파일 내 private 하위 컴포저블로 숨기는 우회를 막기 위함
        val fileName = expression.containingKtFile.name
        if (!fileName.endsWith("Screen.kt")) return

        val packageName = expression.containingKtFile.packageFqName.asString()
        if (!packageName.contains("feature")) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "Screen 파일에서 Scaffold를 직접 호출할 수 없다. " +
                    "Scaffold와 레이아웃 코드는 *Content로 옮기고, " +
                    "Screen은 상태 구독·SideEffect 처리·Content 위임만 담당한다.",
            )
        )
    }
}
