package com.udstu.fraxinus.authserver

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import com.udstu.fraxinus.authserver.controller.*
import com.udstu.fraxinus.authserver.dto.*
import com.udstu.fraxinus.authserver.config.*
import io.ktor.util.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
fun Application.module() {
    startup()

    routing {
        val config by inject<ServerConfig>()
        authServer()
        sessionServer()
        get("/") {
            call.respond(ServerMetaModel(
                mapOf(
                    "serverName" to config.serverName,
                    "implementationName" to config.implementationName,
                    "implementationVersion" to config.implementationVersion
                ),
                config.skinDomains,
                config.publicKey
            ))
        }
    }
}
