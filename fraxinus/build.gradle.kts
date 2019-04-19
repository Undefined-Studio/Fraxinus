@file: Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val mainClassName = "io.ktor.server.netty.EngineMain"

dependencies {
    implementation(project(":common"))
    implementation(deps("ktor-auth"))
    implementation(deps("ktor-auth-jwt"))
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}
