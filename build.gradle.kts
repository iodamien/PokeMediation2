plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    kotlin("plugin.compose") version "2.1.20"
    id("org.jetbrains.compose") version "1.9.0-rc01"
}

group = "com.lumeen.platform"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://maven.pkg.github.com/iodamien/irobax-client")
        credentials {
            username = "iodamien"
            password = "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"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.kaml)
    implementation(compose.desktop.currentOs)
    implementation(libs.viewmodel)
    implementation(libs.richeditor.compose)

    implementation("com.irobax.uikit:uikit:1.3-SNAPSHOT")
    implementation("com.irobax.record:record:1.0-SNAPSHOT")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}