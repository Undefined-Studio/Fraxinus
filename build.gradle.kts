@file: Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin") version Versions.KOTLIN_VERSION
    id("shadow") version Versions.SHADOW_VERSION apply false
    id("frontend") version Versions.KOTLIN_FRONTEND_VERSION apply false
    id("serialization") version Versions.KOTLIN_VERSION apply false
}

group = "com.udstu"
version = "0.1.0"

subprojects {
    if (project.name == "fraxinus-web") {
        apply(plugin = "kotlin2js")
        apply(plugin = "kotlinx-serialization")
        apply(plugin = "org.jetbrains.kotlin.frontend")

    } else {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "com.github.johnrengelman.shadow")

        repositories {
            maven {
                url = uri("https://kotlin.bintray.com/ktor")
            }
        }

        dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(deps("ktor-server-netty"))
            implementation(deps("logback"))
            implementation(deps("ktor-server-core"))
            implementation(deps("koin-ktor"))
            implementation(deps("ktor-jackson"))
            testImplementation(deps("ktor-server-test"))
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = Versions.JAVA_VERSION
        }
    }

    repositories {
        mavenCentral()
        jcenter()
    }
}
