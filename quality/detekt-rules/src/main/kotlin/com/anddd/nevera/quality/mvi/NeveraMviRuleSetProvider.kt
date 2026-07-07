package com.anddd.nevera.quality.mvi

import com.anddd.nevera.quality.mvi.rules.ContentComposableParameterRule
import com.anddd.nevera.quality.mvi.rules.NeveraViewModelInheritanceRule
import com.anddd.nevera.quality.mvi.rules.ReduceOutsideApplyMutationRule
import com.anddd.nevera.quality.mvi.rules.SealedInterfaceContractRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class NeveraMviRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "NeveraMviRules"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            NeveraViewModelInheritanceRule(config),
            ReduceOutsideApplyMutationRule(config),
            SealedInterfaceContractRule(config),
            ContentComposableParameterRule(config),
        )
    )
}
