package com.udstu.fraxinus.asgard.dto

class TexturePropertyModel(
    profileId: String,
    profileName: String,
    timestamp: Long,
    val textures: Map<String, TextureModel>
) : ProfilePropertyModel(profileId, profileName, timestamp)
