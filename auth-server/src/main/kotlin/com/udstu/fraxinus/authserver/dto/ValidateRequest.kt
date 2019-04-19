package com.udstu.fraxinus.authserver.dto

data class ValidateRequest(val accessToken: String, val clientToken: String? = null)
