package com.anddd.nevera.quality.designsystem

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class NeveraDesignSystemRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "NeveraDesignSystemRules"

    override fun instance(config: Config) = RuleSet(ruleSetId, emptyList())
}
