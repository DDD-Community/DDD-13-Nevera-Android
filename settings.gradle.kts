pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Nevera"
include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:ui")
include(":core:network")
include(":infra:notification")
include(":infra:permission")
include(":core:database")
include(":core:mvi")
include(":core:navigation")
include(":domain")
include(":data")
include(":feature:splash:impl")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:main:api")
include(":feature:main:impl")
include(":feature:mypage:api")
include(":feature:mypage:impl")
include(":feature:notification:api")
include(":feature:notification:impl")
include(":feature:sample")
include(":feature:ingredient:api")
include(":feature:ingredient:impl")
include(":feature:fridge:api")
include(":feature:fridge:impl")
include(":quality:detekt-rules")
