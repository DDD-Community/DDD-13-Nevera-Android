plugins {
    id("nevera.kotlin.jvm")
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.javax.inject)
    implementation(libs.coroutines.core)

    testImplementation(libs.coroutines.test)
}
