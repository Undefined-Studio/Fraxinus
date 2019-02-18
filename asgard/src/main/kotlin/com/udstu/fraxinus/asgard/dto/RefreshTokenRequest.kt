package com.udstu.fraxinus.asgard.dto

data class RefreshTokenRequest(
    val accessToken: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
    val selectedProfile: ProfileModel? = null
)
