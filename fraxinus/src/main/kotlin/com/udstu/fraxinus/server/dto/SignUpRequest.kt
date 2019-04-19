package com.udstu.fraxinus.server.dto

data class SignUpRequest(
    val username: String,
    val password: String,
    val characterName: String
)
