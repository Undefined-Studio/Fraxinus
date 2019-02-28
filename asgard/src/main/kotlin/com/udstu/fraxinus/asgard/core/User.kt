package com.udstu.fraxinus.asgard.core

import com.udstu.fraxinus.asgard.dto.*

class User(val id: String, val username: String, val characters: List<Character>, val properties: List<Map<String, String>>) {

    fun toUserModel(): UserModel {
        return UserModel(
            id,
            properties
        )
    }
}
