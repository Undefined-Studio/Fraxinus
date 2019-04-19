package com.udstu.fraxinus.server.controller

import com.udstu.fraxinus.server.dto.SignUpRequest
import com.udstu.fraxinus.server.service.AuthService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject

const val AUTH_PREFIX = "api/auth"

fun Route.authController() {
    val authService by inject<AuthService>()

    post("$AUTH_PREFIX/signUp") {
        val req = call.receive<SignUpRequest>()
        authService.signUp(req)
        call.respond(HttpStatusCode.Created)
    }
}