package com.udstu.fraxinus.midgard.server

import com.udstu.fraxinus.midgard.server.config.startup
import com.udstu.fraxinus.midgard.server.controller.*
import io.ktor.application.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
fun Application.module() {
    startup()

    routing {
        authController()
        static {
            static("static") {
                resources("web/static")
            }
            resources("web")
            defaultResource("web/index.html")
        }
    }
}
