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
            implementationClass = "com.anddd.nevera.buildlogic.NeveraTestUnitPlugin"
        }
        register("neveraTestAndroid") {
            id = "nevera.test.android"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraTestAndroidPlugin"
        }
        register("neveraNetwork") {
            id = "nevera.network"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraNetworkPlugin"
        }
        register("neveraFirebase") {
            id = "nevera.firebase"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraFirebasePlugin"
        }
        register("neveraKotlinJvm") {
            id = "nevera.kotlin.jvm"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraKotlinJvmPlugin"
        }
        register("neveraAndroidLibrary") {
            id = "nevera.android.library"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraAndroidLibraryPlugin"
        }
        register("neveraAndroidCompose") {
            id = "nevera.android.compose"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraAndroidComposePlugin"
        }
        register("neveraAndroidHilt") {
            id = "nevera.android.hilt"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraAndroidHiltPlugin"
        }
        register("neveraFeature") {
            id = "nevera.feature"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraFeaturePlugin"
        }
        register("neveraAndroidApplication") {
            id = "nevera.android.application"
            implementationClass = "com.anddd.nevera.buildlogic.NeveraAndroidApplicationPlugin"
        }
    }
}
