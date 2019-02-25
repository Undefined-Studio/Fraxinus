package com.udstu.fraxinus.asgard.dto

data class LoginResponse(
    val accessToken: String,
    val clientToken: String,
    val availableProfiles: List<ProfileModel>?,
    val selectedProfile: ProfileModel? = null,
    val user: UserModel? = null
)
