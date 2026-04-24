pluginManagement {
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
include(":core:database")
include(":domain")
include(":data")
include(":feature:login")
include(":feature:main")
include(":feature:splash")
include(":feature:signup")
