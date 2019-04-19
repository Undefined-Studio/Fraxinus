package com.udstu.fraxinus.common.core.store

import com.udstu.fraxinus.common.core.*
import com.udstu.fraxinus.common.dao.*
import com.udstu.fraxinus.common.dao.entity.*
import com.udstu.fraxinus.common.util.*
import org.slf4j.LoggerFactory

object UserStore {
    private val logger = LoggerFactory.getLogger(UserStore::class.java)

    suspend fun getAuthenticatedUser(username: String, password: String): User? {
        return Users.findByUsernameAndPassword(username, password.encodeMD5())?.let {
            getUser(it)
        }
    }

    suspend fun createUser(user: User, password: String) {
        val userEntity = UserEntity(user.id, user.username, password.encodeMD5())

        Users.save(userEntity).also {
            logger.info("Created user(id: ${userEntity.id})")
        }

        user.properties.map {
            UserPropertyEntity(null, it.getValue("name"), it.getValue("value"), userEntity.id)
        }.forEach {
            UserProperties.save(it)
        }

        user.characters.map {
            CharacterStore.createCharacter(it, user)
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
