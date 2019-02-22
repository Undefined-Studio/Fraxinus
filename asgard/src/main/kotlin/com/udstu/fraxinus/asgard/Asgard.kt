package com.udstu.fraxinus.asgard

import com.fasterxml.jackson.annotation.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.asgard.controller.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.service.*
import io.ktor.jackson.*
import com.udstu.fraxinus.helheim.dao.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.koin.Logger.SLF4JLogger
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.installKoin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
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

    installKoin {
        modules(asgardDependencies)
    }

    DataResource.init(
        environment.config.propertyOrNull("database.url")?.getString()?:"",
        environment.config.propertyOrNull("database.username")?.getString()?:"",
        environment.config.propertyOrNull("database.password")?.getString()?:""
    )

    transaction {
        SchemaUtils.create(Capes, ProfileProperties, Profiles, Skins, UserProperties, Users, Tokens)
    }

    routing {
        authServer()
        get("/") {
            call.respondText("Hello from Asgard!", contentType = ContentType.Text.Plain)
        }
    }
}

// Declare Dependencies

val asgardDependencies = module {
    single { AuthServerService }
}
