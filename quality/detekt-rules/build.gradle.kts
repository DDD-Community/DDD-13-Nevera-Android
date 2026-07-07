plugins {
    id("nevera.kotlin.jvm")
}

dependencies {
    compileOnly(libs.detekt.api)
    testImplementation(libs.detekt.test)
}
