package com.udstu.fraxinus.asgard.core.store

import com.udstu.fraxinus.asgard.core.*
import com.udstu.fraxinus.asgard.dao.*
import com.udstu.fraxinus.asgard.dao.entity.*
import com.udstu.fraxinus.asgard.util.*

object UserStore {
    suspend fun getAuthenticatedUser(username: String, password: String): User? {
        return Users.findByUsernameAndPassword(username, password.encodeMD5())?.let {
            getUser(it)
        }
    }

    suspend fun getUser(id: String): User? {
        return Users.find(id)?.let {
            UserStore.getUser(it)
        }
    }

    private suspend fun getUser(userEntity: UserEntity, simpleCharacter: Boolean = true): User {
        val properties = UserProperties.findByUserId(userEntity.id).map {
            mapOf(
                "name" to it.name,
                "value" to it.value
            )
        }

        val characters = CharacterStore.getCharacterByUserId(userEntity.id, simpleCharacter)

        return User(
            userEntity.id,
            userEntity.username,
            characters,
            properties
        )
    }
}