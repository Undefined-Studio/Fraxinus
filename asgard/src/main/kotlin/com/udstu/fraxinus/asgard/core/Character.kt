package com.udstu.fraxinus.asgard.core

import com.udstu.fraxinus.asgard.core.base.ProfileProperty
import com.udstu.fraxinus.asgard.dto.*

class Character(val id: String, val name: String, val properties: List<ProfileProperty>? = null) {

    fun toProfileModel(): ProfileModel {
        if (properties == null)
            return ProfileModel(id, name)

        return ProfileModel(
            id,
            name,
            properties.map {
                mapOf(
                    "name" to it.name,
                    "value" to it.generateValue()
                )
            }
        )
    }
}
