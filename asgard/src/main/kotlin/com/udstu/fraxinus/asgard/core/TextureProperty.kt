package com.udstu.fraxinus.asgard.core

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*
import com.udstu.fraxinus.asgard.core.base.*
import java.util.*

class TextureProperty(
    name: String,
    timestamp: Long,
    profileName: String,
    val texture: Map<String, Texture>) : ProfileProperty(name, timestamp, profileName) {

    override fun generateValue(): String {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return Base64.getEncoder().encode(mapper.writeValueAsString(this).toByteArray()).toString(Charsets.UTF_8)
    }
}