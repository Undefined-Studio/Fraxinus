package com.udstu.fraxinus.authserver.dto

data class InvalidateRequest(val accessToken: String?, val clientToken: String? = null)
