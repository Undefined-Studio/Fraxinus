package com.udstu.fraxinus.server.config

import com.udstu.fraxinus.common.config.*
import com.udstu.fraxinus.server.service.AuthService
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


