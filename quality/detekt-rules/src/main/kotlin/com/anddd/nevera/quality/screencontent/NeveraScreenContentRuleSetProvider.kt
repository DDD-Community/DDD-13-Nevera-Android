package com.anddd.nevera.quality.screencontent

import com.anddd.nevera.quality.screencontent.rules.ScreenDelegatesToContentRule
import com.anddd.nevera.quality.screencontent.rules.ScreenNoScaffoldRule
import com.anddd.nevera.quality.screencontent.rules.ToastOutsideScreenRule
import com.anddd.nevera.quality.screencontent.rules.ViewModelAccessOnlyInScreenRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class NeveraScreenContentRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "NeveraScreenContentRules"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            ScreenDelegatesToContentRule(config),
            ScreenNoScaffoldRule(config),
            ViewModelAccessOnlyInScreenRule(config),
            ToastOutsideScreenRule(config),
        )
    )
}
