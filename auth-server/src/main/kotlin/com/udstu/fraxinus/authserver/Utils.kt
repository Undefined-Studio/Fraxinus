package com.udstu.fraxinus.authserver

import com.udstu.fraxinus.authserver.dto.*
import com.udstu.fraxinus.common.core.*

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
