package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.cache.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.helheim.dao.*
import org.jetbrains.exposed.sql.*

class SessionServerService(
    private val authenticator: SessionAuthenticator,
    private val tokenStore: TokenStore
) {

    suspend fun joinServer(req: JoinServerRequest, serverIp: String?) {
        if (req.accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NULL_SERVER_ID)
        if (req.selectedProfile == null) throw AsgardException.IllegalArgumentException(AsgardException.NULL_SELECTED_PROFILE)

        val token = tokenStore.validateToken(req.accessToken, null)

        if (token.selectedCharacter == null || token.selectedCharacter.id != req.selectedProfile)
            throw AsgardException.IllegalArgumentException(AsgardException.INVALID_PROFILE)

        authenticator.joinServer(token, req.serverId, serverIp)
    }

    suspend fun hasJoinedServer(serverId: String?, username: String?, ip: String?): ProfileModel? {
        if (serverId != null && username != null) {
            return authenticator.verifyUser(username, serverId, ip)
        }

        return null
    }

    suspend fun findProfile(profileId: String?, unsigned: Boolean = true): ProfileModel? {
        if (profileId == null) return null

        return query {
            Profiles.select {
                Profiles.id eq profileId
            }.map {
                if (unsigned) {
                    generateProfileModel(it)
                } else {
                    generateProfileModel(it)
                }
            }.firstOrNull()
        }
    }

    suspend fun queryProfiles(req: List<String>) : List<ProfileModel> {
        return req.mapNotNull {
            query {
                Profiles.select {
                    Profiles.name eq it
                }.map { result ->
                    generateProfileSimpleModel(result)
                }.firstOrNull()
            }
        }
    }
}