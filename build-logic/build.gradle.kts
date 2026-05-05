plugins {
    `kotlin-dsl`
}

group = "com.anddd.nevera.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("neveraTestUnit") {
            id = "nevera.test.unit"
            implementationClass = "NeveraTestUnitPlugin"
        }
        register("neveraTestAndroid") {
            id = "nevera.test.android"
            implementationClass = "NeveraTestAndroidPlugin"
        }
        register("neveraNetwork") {
            id = "nevera.network"
            implementationClass = "NeveraNetworkPlugin"
        }
        register("neveraFirebase") {
            id = "nevera.firebase"
            implementationClass = "NeveraFirebasePlugin"
        }
        register("neveraKotlinJvm") {
            id = "nevera.kotlin.jvm"
            implementationClass = "NeveraKotlinJvmPlugin"
        }
        register("neveraAndroidLibrary") {
            id = "nevera.android.library"
            implementationClass = "NeveraAndroidLibraryPlugin"
        }
        register("neveraAndroidCompose") {
            id = "nevera.android.compose"
            implementationClass = "NeveraAndroidComposePlugin"
        }
        register("neveraAndroidHilt") {
            id = "nevera.android.hilt"
            implementationClass = "NeveraAndroidHiltPlugin"
        }
        register("neveraFeature") {
            id = "nevera.feature"
            implementationClass = "NeveraFeaturePlugin"
        }
        register("neveraAndroidApplication") {
            id = "nevera.android.application"
            implementationClass = "NeveraAndroidApplicationPlugin"
        }
    }
}
