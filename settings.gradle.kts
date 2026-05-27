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
include(":domain")
include(":data")
include(":feature:splash")
include(":feature:auth")
include(":feature:main")
include(":feature:mypage")
include(":feature:sample")
include(":feature:ingredient")
