package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

class NeveraViewModelInheritanceRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "NeveraViewModelInheritanceRule",
        severity = Severity.Defect,
        description = "feature 모듈의 ViewModel은 반드시 NeveraViewModel을 상속해야 한다.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val name = klass.name ?: return
        if (!name.endsWith("ViewModel")) return

        val packageName = klass.containingKtFile.packageFqName.asString()
        if (!packageName.contains("feature")) return

        val superTypeNames = klass.superTypeListEntries
            .mapNotNull { it.typeAsUserType?.referencedName }

        if ("NeveraViewModel" !in superTypeNames) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(klass),
                    "$name 은 NeveraViewModel을 상속해야 한다. ViewModel()을 직접 상속하지 않는다.",
                )
            )
        }
    }
}
