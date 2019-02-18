package com.udstu.fraxinus.asgard

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import com.udstu.fraxinus.helheim.dao.*
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

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
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DataResource.init(
        environment.config.propertyOrNull("database.url")?.getString()?:"",
        environment.config.propertyOrNull("database.username")?.getString()?:"",
        environment.config.propertyOrNull("database.password")?.getString()?:""
    )

    transaction {
        SchemaUtils.create(Capes, ProfileProperties, Profiles, Skins, UserProperties, Users)
    }

    routing {
        get("/") {
            call.respondText("Hello from Asgard!", contentType = ContentType.Text.Plain)
        }
    }
}
