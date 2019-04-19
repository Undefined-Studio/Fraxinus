package com.udstu.fraxinus.common.config

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.common.dao.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.slf4j.event.*

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


fun Application.installJackson() {
    install(ContentNegotiation) {
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

fun Application.installLogger() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}
