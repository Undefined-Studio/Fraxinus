package com.udstu.fraxinus.authserver.config

import com.udstu.fraxinus.authserver.cache.*
import com.udstu.fraxinus.authserver.dto.*
import com.udstu.fraxinus.authserver.exception.*
import com.udstu.fraxinus.authserver.service.*
import com.udstu.fraxinus.common.config.*
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
            environment.config.propertyOrNull("authserver.session.expireDuration")?.getString()?.toLong()?:15L
        )) }
        single { SessionServerService(get(), get()) }
        single {

            val privateKeyStr = (environment.config.propertyOrNull("authserver.privateKey")?.getString() ?: "pri-key")
                .replace(" ", "")
                .split('\n')
                .filterNot {
                    it.startsWith('-')
                }.joinToString("")

            val publicKeyStr = (environment.config.propertyOrNull("authserver.publicKey")?.getString()?.replace(" ", "") ?: "pub-key")
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
                environment.config.propertyOrNull("authserver.serverName")?.getString() ?: "Asgard",
                environment.config.propertyOrNull("authserver.implementationName")?.getString() ?: "authserver",
                environment.config.propertyOrNull("authserver.implementationVersion")?.getString() ?: "dev",
                environment.config.propertyOrNull("authserver.skinDomains")?.getList() ?: listOf(".*.com")
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
