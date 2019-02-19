package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.AsgardException
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.enum.TextureType
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        block()
    }
}

fun generateUserModel(result: ResultRow): UserModel {
    return UserModel(result[Users.id], UserProperties.select {
        UserProperties.userId eq result[Users.id]
    }.map { propertyResult ->
        UserPropertyModel(propertyResult[UserProperties.name], propertyResult[UserProperties.value])
    })
}


fun generateProfileModel(result: ResultRow): ProfileModel {
    val profileId = result[Profiles.id]
    val profileName = result[Profiles.name]
    val profileSkinId = result[Profiles.skinId]
    val profileCapeId = result[Profiles.capeId]
    return ProfileModel(
        profileId,
        profileName,
        ProfileProperties.select {
            ProfileProperties.profileId eq profileId
        }.map { profilePropertyResult ->
            if (profileSkinId == null && profileCapeId == null)
                TexturePropertyModel(
                    profileId,
                    profileName,
                    profilePropertyResult[ProfileProperties.timestamp].millis
                )
            else {
                val metadata = mutableMapOf<String, TextureModel>()

                // 生成Cape信息
                if (profileCapeId != null) {
                    metadata[TextureType.CAPE.toString()] = Capes.select {
                        Capes.id eq profileCapeId
                    }.map {
                        CapeModel(it[Capes.url])
                    }.firstOrNull() ?: throw AsgardException.IllegalArgumentException(AsgardException.CAPE_NOT_FOUND)
                }

                // 生成Skin信息
                if (profileSkinId != null) {
                    metadata[TextureType.CAPE.toString()] = Skins.select {
                        Skins.id eq profileSkinId
                    }.map {
                        SkinModel(
                            it[Skins.url],
                            it[Skins.model]
                        )
                    }.firstOrNull() ?: throw AsgardException.IllegalArgumentException(AsgardException.CAPE_NOT_FOUND)
                }

                TexturePropertyModel(
                    profileId,
                    profileName,
                    profilePropertyResult[ProfileProperties.timestamp].millis,
                    metadata
                )
            }
        }
    )
}
