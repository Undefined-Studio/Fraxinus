package com.udstu.fraxinus.asgard

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.helheim.core.*

fun Character.toProfileModel(): ProfileModel {
    if (properties == null)
        return ProfileModel(id, name)

    return ProfileModel(
        id,
        name,
        this.properties!!.map {
            mapOf(
                "name" to it.name,
                "value" to it.generateValue()
            )
        }
    )
}

fun User.toUserModel(): UserModel {
    return UserModel(
        id,
        properties
    )
}
