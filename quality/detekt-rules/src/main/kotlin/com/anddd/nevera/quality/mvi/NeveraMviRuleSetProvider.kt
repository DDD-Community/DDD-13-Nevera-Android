package com.anddd.nevera.quality.mvi

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class NeveraMviRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "NeveraMviRules"

    override fun instance(config: Config) = RuleSet(ruleSetId, emptyList())
}
