package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class NeveraKotlinJvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("nevera.test.unit")

            configure<KotlinJvmProjectExtension> {
                jvmToolchain(17)
            }
        }
    }
}
