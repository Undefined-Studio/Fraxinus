package com.udstu.fraxinus.asgard

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.udstu.fraxinus.asgard.controller.*
import com.udstu.fraxinus.asgard.dto.ServerMetaModel
import com.udstu.fraxinus.asgard.server.ServerConfig
import com.udstu.fraxinus.asgard.server.startup
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
