@file: Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("application")
}

val mainClassName = "io.ktor.server.netty.EngineMain"

dependencies {
    implementation(project(":common"))
    implementation(deps("concurrent-map"))
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}
