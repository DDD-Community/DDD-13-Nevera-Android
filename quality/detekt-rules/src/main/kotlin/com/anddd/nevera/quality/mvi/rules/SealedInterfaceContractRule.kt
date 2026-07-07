package com.anddd.nevera.quality.mvi.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

class SealedInterfaceContractRule(config: Config) : Rule(config) {

    private val contractSuffixes = listOf("Intent", "Mutation", "SideEffect")

    override val issue = Issue(
        id = "SealedInterfaceContractRule",
        severity = Severity.Defect,
        description = "*Intent, *Mutation, *SideEffect 타입은 반드시 sealed interface로 선언해야 한다.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val name = klass.name ?: return
        val matchedSuffix = contractSuffixes.firstOrNull { name.endsWith(it) } ?: return

        val isSealed = klass.isSealed()
        val isInterface = klass.isInterface()

        if (!isSealed || !isInterface) {
            val actual = when {
                isSealed && !isInterface -> "sealed class"
                !isSealed && isInterface -> "interface"
                !isSealed && !isInterface -> "class"
                else -> "알 수 없는 형태"
            }
            report(
                CodeSmell(
                    issue,
                    Entity.from(klass),
                    "$name (접미사: $matchedSuffix)은 sealed interface로 선언해야 한다. 현재: $actual",
                )
            )
        }
    }
}
