package com.udstu.fraxinus.helheim.core

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.helheim.core.base.*
import java.util.*

class TextureProperty(
    name: String,
    timestamp: Long,
    profileName: String,
    profileId: String,
    val textures: Map<String, Texture>) : ProfileProperty(name, timestamp, profileName, profileId) {

    override fun generateValue(): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return Base64.getEncoder().encode(mapper.writeValueAsString(this).toByteArray()).toString(Charsets.UTF_8)
    }
}
