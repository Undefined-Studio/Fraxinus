package com.udstu.fraxinus.asgard.dto

data class ValidateRequest(val accessToken: String, val clientToken: String? = null)
