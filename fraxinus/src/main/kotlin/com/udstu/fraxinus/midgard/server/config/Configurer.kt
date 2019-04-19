package com.udstu.fraxinus.midgard.server.config

import com.udstu.fraxinus.helheim.config.*
import com.udstu.fraxinus.midgard.server.service.AuthService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.*
import org.koin.dsl.module

@KtorExperimentalAPI
fun Application.startup() {
    install(Authentication) {

    }

    installJackson()

    installLogger()

    val midgardDependencies = module {
        single { AuthService() }
    }

    installDataResource()
}


