package com.udstu.fraxinus.helheim.core.store

import com.udstu.fraxinus.helheim.core.*
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.dao.entity.*
import com.udstu.fraxinus.helheim.util.*

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
