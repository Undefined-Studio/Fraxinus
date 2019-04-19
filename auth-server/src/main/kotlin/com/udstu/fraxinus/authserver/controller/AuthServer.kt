package com.udstu.fraxinus.asgard.controller

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.service.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.*

fun Route.authServer() {

    val authServerService by inject<AuthServerService>()

    post("/authserver/authenticate") {
        val req = call.receive<LoginRequest>()
        call.respond(authServerService.login(req))
    }

    post("/authserver/refresh") {
        val req = call.receive<RefreshTokenRequest>()
        call.respond(authServerService.refresh(req))
    }

    post("/authserver/validate") {
        val req = call.receive<ValidateRequest>()
        authServerService.validate(req)
        call.respond(HttpStatusCode.NoContent)
    }

    post("/authserver/invalidate") {
        val req = call.receive<InvalidateRequest>()
        try {
            authServerService.invalidate(req)
        } catch (e: AsgardException) {
        } finally {
            call.respond(HttpStatusCode.NoContent)
        }
    }

    post("/authserver/signout") {
        val req = call.receive<SignOutRequest>()
        authServerService.signOut(req)
        call.respond(HttpStatusCode.NoContent)
    }
}
