package com.udstu.fraxinus.asgard.dto

data class InvalidateRequest(val accessToken: String, val clientToken: String? = null)
