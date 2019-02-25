package com.udstu.fraxinus.asgard.server

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.asgard.cache.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.service.*
import com.udstu.fraxinus.helheim.dao.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.koin.dsl.*
import org.koin.ktor.ext.*
import org.slf4j.event.*
import java.time.*

@KtorExperimentalAPI
fun Application.installDataResource() {
    DataResource.init(
        environment.config.propertyOrNull("database.url")?.getString()?:"",
        environment.config.propertyOrNull("database.username")?.getString()?:"",
        environment.config.propertyOrNull("database.password")?.getString()?:""
    )

    // Auto create tables
    transaction {
        SchemaUtils.create(Capes, ProfileProperties, Profiles, Skins, UserProperties, Users, Tokens)
    }
}


// Startup
@KtorExperimentalAPI
fun Application.startup() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        exception<AsgardException> { cause ->
            call.respond(cause.status, ErrorModel(cause.error, cause.message!!))
        }
    }

    // Declare Dependencies
    val asgardDependencies = module {
        single { TokenStore() }
        single { AuthServerService(get()) }
        single { SessionAuthenticator(Duration.ofSeconds(
            environment.config.propertyOrNull("asgard.session.expireDuration")?.getString()?.toLong()?:15L
        )) }
        single { SessionServerService(get(), get()) }
        single {
            val serverName = environment.config.propertyOrNull("asgard.serverName")?.getString()?:"Asgard"
            val implementationName = environment.config.propertyOrNull("asgard.implementationName")?.getString()?:"asgard"
            val implementationVersion = environment.config.propertyOrNull("asgard.implementationVersion")?.getString()?:"dev"
            val skinDomains = environment.config.propertyOrNull("asgard.skinDomains")?.getList()?: listOf(".*.com")
            val pubKey = environment.config.propertyOrNull("asgard.publicKey")?.getString()?:"pub-key"
            ServerMetaModel(
                mapOf(
                    "serverName" to serverName,
                    "implementationName" to implementationName,
                    "implementationVersion" to implementationVersion
                ),
                skinDomains,
                pubKey
            )}
    }

    installKoin {
        modules(asgardDependencies)
    }

    installDataResource()
}