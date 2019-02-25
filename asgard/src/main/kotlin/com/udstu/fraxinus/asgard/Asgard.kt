package com.udstu.fraxinus.asgard

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.udstu.fraxinus.asgard.controller.*
import com.udstu.fraxinus.asgard.dto.ServerMetaModel
import com.udstu.fraxinus.asgard.server.startup
import io.ktor.util.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
fun Application.module() {
    startup()

    routing {
        val info by inject<ServerMetaModel>()
        authServer()
        sessionServer()
        get("/") {
            call.respond(info)
        }
    }
}
