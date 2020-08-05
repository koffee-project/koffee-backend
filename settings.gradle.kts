pluginManagement {
    val kotlinVersion: String by settings
    val ktlintVersion: String by settings
    val mongoGradlePluginVersion: String by settings
    val shadowVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.dokka") version kotlinVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
        id("com.github.johnrengelman.shadow") version shadowVersion
        id("com.sourcemuse.mongo") version mongoGradlePluginVersion
    }
}

rootProject.name = "koffee-backend"
