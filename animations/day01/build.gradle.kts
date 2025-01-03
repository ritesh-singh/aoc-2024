plugins {
    kotlin("jvm") version "2.1.0"
    id("application")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

application {
    mainClass.set("day01.Main")
}

dependencies {
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")
    implementation("com.jakewharton.mosaic:mosaic-runtime:0.14.0")
}
