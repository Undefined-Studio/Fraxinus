package com.udstu.fraxinus.asgard.core

import com.udstu.fraxinus.asgard.core.base.ProfileProperty
import com.udstu.fraxinus.asgard.core.base.Texture
import com.udstu.fraxinus.asgard.dao.Capes
import com.udstu.fraxinus.asgard.dao.Skins
import com.udstu.fraxinus.asgard.dao.entity.ProfileEntity
import com.udstu.fraxinus.asgard.dao.entity.ProfilePropertyEntity
import com.udstu.fraxinus.asgard.enum.TextureType

object ProfilePropertyFactory {
    const val TEXTURE = "texture"

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
                    textures
                )
            }
            else -> null
        }
    }
}