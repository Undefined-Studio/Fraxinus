package com.udstu.fraxinus.asgard.exception

import io.ktor.http.HttpStatusCode
import java.lang.RuntimeException

class AsgardException(
    val status: HttpStatusCode,
    val error: String, message: String
) : RuntimeException(message) {

    companion object {
        fun ForbiddenOperationException(message: String) = AsgardException(HttpStatusCode.Forbidden, "ForbiddenOperationException", message)

        fun IllegalArgumentException(message: String) = AsgardException(HttpStatusCode.BadRequest, "IllegalArgumentException", message)

        const val INVALID_TOKEN = "Invalid token."
        const val INVALID_CREDENTIALS = "Invalid credentials. Invalid username or password."
        const val TOKEN_ALREADY_ASSIGNED = "Access token already has a profile assigned."
        const val ACCESS_DENIED = "Access denied."
        const val PROFILE_NOT_FOUND = "No such profile."
        const val CAPE_NOT_FOUND = "No such cape."
        const val NO_CREDENTIALS = "credentials is null."
    }
}