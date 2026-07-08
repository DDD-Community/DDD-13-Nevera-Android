package com.anddd.nevera.quality.designsystem.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class Material3AppBarRule(config: Config) : Rule(config) {

    private val forbiddenAppBars = setOf(
        "TopAppBar",
        "CenterAlignedTopAppBar",
        "SmallTopAppBar",
        "MediumTopAppBar",
        "LargeTopAppBar",
    )

    override val issue = Issue(
        id = "Material3AppBarRule",
        severity = Severity.Defect,
        description = "Material3 기본 AppBar 대신 디자인 시스템의 NeveraAppBar 계열을 사용해야 한다.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression?.text ?: return
        if (callee in forbiddenAppBars) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "$callee 사용 금지. NeveraAppBar, NeveraDisplayAppBar, " +
                        "NeveraLogoAppBar, NeveraSearchAppBar 중 적합한 것을 사용하라.",
                )
            )
        }
    }
}
