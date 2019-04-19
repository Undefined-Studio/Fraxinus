package com.udstu.fraxinus.authserver.controller

import com.udstu.fraxinus.authserver.dto.*
import com.udstu.fraxinus.authserver.service.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.*

fun Route.sessionServer() {
    val sessionServerService by inject<SessionServerService>()

    post("/sessionserver/session/minecraft/join") {
        val req = call.receive<JoinServerRequest>()
        sessionServerService.joinServer(req, call.request.host())
        call.respond(HttpStatusCode.NoContent)
    }

    get("/sessionserver/session/minecraft/hasJoined") {
        val params =  call.request.queryParameters
        val profile = sessionServerService.hasJoinedServer(
            params["serverId"],
            params["username"],
            params["ip"]
        )

        if (profile != null) {
            call.respond(profile)
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }

    get("/sessionserver/session/minecraft/profile/{id}") {
        val id = call.parameters["id"]
        val unsigned = call.parameters["unsigned"]?.toBoolean()
        val profile = if (unsigned != null) {
            sessionServerService.findProfile(id, unsigned)
        } else {
            sessionServerService.findProfile(id)
        }

        if (profile == null) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(profile)
        }
    }

    post("/api/profiles/minecraft") {
        val req = call.receive<List<String>>()
        call.respond(sessionServerService.queryProfiles(req))
    }

}
