package com.udstu.fraxinus.common.core

import com.udstu.fraxinus.common.core.base.*
import com.udstu.fraxinus.common.dao.*
import com.udstu.fraxinus.common.dao.entity.*
import com.udstu.fraxinus.common.enum.*

object ProfilePropertyFactory {
    const val TEXTURE = "textures"

    suspend fun create(profileEntity: ProfileEntity, profilePropertyEntity: ProfilePropertyEntity): ProfileProperty? {
        return when(profilePropertyEntity.name.toLowerCase()) {
            TEXTURE -> {
                val textures = mutableMapOf<String, Texture>()

                profileEntity.skinId?.also {
                    Skins.find(it)?.let { entity ->
                        Skin(entity.url, entity.model)
                    }?.also { skin ->
                        textures[TextureType.SKIN.toString()] = skin
                    }
                }

                profileEntity.capeId?.also {
                    Capes.find(it)?.let { entity ->
                        Cape(entity.url)
                    }?.also { cape ->
                        textures[TextureType.CAPE.toString()] = cape
                    }
                }

                TextureProperty(
                    profilePropertyEntity.name,
                    profilePropertyEntity.timestamp.time,
                    profilePropertyEntity.profileName,
                    profilePropertyEntity.profileId,
                    textures
                )
            }
            else -> null
        }
    }
}
