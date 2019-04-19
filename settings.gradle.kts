@file: Suppress("SpellCheckingInspection", "UnstableApiUsage")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "shadow") {
                useModule("com.github.jengelman.gradle.plugins:shadow:${requested.version}")
            }
            if (requested.id.id == "frontend") {
                useModule("org.jetbrains.kotlin:kotlin-frontend-plugin:${requested.version}")
            }
            if (requested.id.id == "serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }

        repositories {
            mavenCentral()
            jcenter()
            maven {
                url = uri("https://plugins.gradle.org/m2/")
            }
            maven {
                url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
            }
        }
    }
}

rootProject.name = "fraxinus"

enableFeaturePreview("GRADLE_METADATA")

include(":auth-server")
include(":common")
include(":fraxinus-web")
include(":fraxinus")
