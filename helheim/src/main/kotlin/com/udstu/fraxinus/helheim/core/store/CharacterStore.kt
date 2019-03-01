package com.udstu.fraxinus.helheim.core.store

import com.udstu.fraxinus.helheim.core.*
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.dao.entity.*

object CharacterStore {
    suspend fun getCharacterByUserId(id: String, simple: Boolean = true): List<Character> {
        return Profiles.findByUserId(id).map {
            getCharacter(it, simple)
        }
    }

    suspend fun getCharacterByName(name: String, simple: Boolean = true): Character? {
        return Profiles.findByName(name)?.let {
            getCharacter(it, simple)
        }
    }

    suspend fun getCharacterById(id: String, simple: Boolean = true): Character? {
        return Profiles.find(id)?.let {
            getCharacter(it, simple)
        }
    }

    private suspend fun getCharacter(profileEntity: ProfileEntity, simple: Boolean = true) : Character {
        return if (simple) {
            getSimpleCharacter(profileEntity)
        } else {
            getCharacterWithProperty(profileEntity)
        }
    }

    private fun getSimpleCharacter(profileEntity: ProfileEntity) : Character {
        return Character(profileEntity.id, profileEntity.name)
    }

    private suspend fun getCharacterWithProperty(profileEntity: ProfileEntity) : Character {
        val properties = ProfileProperties.findByProfileId(profileEntity.id).mapNotNull {
            ProfilePropertyFactory.create(profileEntity, it)
        }

        return Character(
            profileEntity.id,
            profileEntity.name,
            properties
        )
    }
}
