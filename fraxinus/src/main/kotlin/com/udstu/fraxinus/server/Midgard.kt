package com.udstu.fraxinus.server

import com.udstu.fraxinus.server.config.startup
import com.udstu.fraxinus.server.controller.*
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
fun Application.module() {
    startup()

    routing {
        authController()
    }
}
