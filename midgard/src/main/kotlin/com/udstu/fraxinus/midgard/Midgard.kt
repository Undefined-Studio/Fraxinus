package com.udstu.fraxinus.midgard

import com.udstu.fraxinus.midgard.config.startup
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
fun Application.module() {
    startup()

    routing {

    }
}
