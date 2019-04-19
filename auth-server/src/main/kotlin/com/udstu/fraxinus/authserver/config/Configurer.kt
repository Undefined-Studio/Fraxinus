package com.udstu.fraxinus.asgard.config

import com.udstu.fraxinus.asgard.cache.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.service.*
import com.udstu.fraxinus.helheim.config.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.util.*
import org.koin.dsl.*
import org.koin.ktor.ext.*
import java.security.*
import java.security.spec.*
import java.time.*
import java.util.*

// Startup
@KtorExperimentalAPI
fun Application.startup() {
    installJackson()

    installLogger()

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

            val privateKeyStr = (environment.config.propertyOrNull("asgard.privateKey")?.getString() ?: "pri-key")
                .replace(" ", "")
                .split('\n')
                .filterNot {
                    it.startsWith('-')
                }.joinToString("")

            val publicKeyStr = (environment.config.propertyOrNull("asgard.publicKey")?.getString()?.replace(" ", "") ?: "pub-key")
                .replace(" ", "")
                .split('\n')
                .filterNot {
                    it.startsWith('-')
                }.joinToString("")

            val factory = KeyFactory.getInstance("rsa")

            val spec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr))

            val key = factory.generatePrivate(spec)

            ServerConfig(
                "-----BEGIN PUBLIC KEY-----\n$publicKeyStr\n-----END PUBLIC KEY-----\n",
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
