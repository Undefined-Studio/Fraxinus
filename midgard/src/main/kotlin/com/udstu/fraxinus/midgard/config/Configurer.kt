package com.udstu.fraxinus.midgard.config

import com.udstu.fraxinus.helheim.config.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.*

@KtorExperimentalAPI
fun Application.startup() {
    install(Authentication) {

    }

    installJackson()

    installLogger()

    installDataResource()
}