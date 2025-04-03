pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.1.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "HS_KART"
include(":app")
