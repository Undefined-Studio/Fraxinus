package com.udstu.fraxinus.asgard.dto

data class RefreshTokenResponse(
    val accessToken: String,
    val clientToken: String,
    val selectedProfile: ProfileModel? = null,
    val user: UserModel? = null
)
