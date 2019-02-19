package com.udstu.fraxinus.asgard.dto

class TexturePropertyModel(
    profileId: String,
    profileName: String,
    timestamp: Long,
    val textures: Map<String, TextureModel>? = null
) : ProfilePropertyModel(profileId, profileName, timestamp)
