package com.udstu.fraxinus.asgard.server

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.asgard.cache.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.service.*
import com.udstu.fraxinus.asgard.dao.*
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
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.*
import java.util.*

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
        single { AuthServerService() }
        single { SessionAuthenticator(Duration.ofSeconds(
            environment.config.propertyOrNull("asgard.session.expireDuration")?.getString()?.toLong()?:15L
        )) }
        single { SessionServerService(get(), get()) }
        single {

            val keyStr = (environment.config.propertyOrNull("asgard.privateKey")?.getString() ?: "pri-key")
                .replace(" ", "")
                .split('\n')
                .filterNot {
                    it.startsWith('-')
                }.joinToString("")

            val factory = KeyFactory.getInstance("rsa")

            val spec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyStr))

            val key = factory.generatePrivate(spec)

            ServerConfig(
                environment.config.propertyOrNull("asgard.publicKey")?.getString() ?: "pub-key",
                key,
                environment.config.propertyOrNull("asgard.serverName")?.getString() ?: "Asgard",
                environment.config.propertyOrNull("asgard.implementationName")?.getString() ?: "asgard",
                environment.config.propertyOrNull("asgard.implementationVersion")?.getString() ?: "dev",
                environment.config.propertyOrNull("asgard.skinDomains")?.getList() ?: listOf(".*.com")
            )
        }
    }

    installKoin {
        modules(asgardDependencies)
    }

    installDataResource()
}

data class ServerConfig(
    val publicKey: String,
    val privateKey: PrivateKey,
    val serverName: String,
    val implementationName: String,
    val implementationVersion: String,
    val skinDomains: List<String>
)
