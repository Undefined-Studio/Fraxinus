package com.udstu.fraxinus.midgard.server.dto

data class SignUpRequest(
    val username: String,
    val password: String,
    val characterName: String
)
