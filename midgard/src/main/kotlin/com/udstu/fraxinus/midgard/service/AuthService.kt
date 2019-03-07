package com.udstu.fraxinus.midgard.service

import com.udstu.fraxinus.midgard.dto.*
import com.udstu.fraxinus.helheim.core.store.*
import com.udstu.fraxinus.helheim.core.*
import com.udstu.fraxinus.helheim.util.randomUnsignedUUID

class AuthService {

    suspend fun signUp(req: SignUpRequest) {
        val user = User(
            randomUnsignedUUID(),
            req.username,
            listOf(
                Character(
                    randomUnsignedUUID(),
                    req.characterName
                )
            ),
            emptyList()
        )

        UserStore.createUser(user, req.password)
    }

    suspend fun login(req: LoginRequest): LoginResponse? {
        val user = UserStore.getAuthenticatedUser(req.username, req.password) ?: return null

        // generate token

        return LoginResponse("", "")

    }
}