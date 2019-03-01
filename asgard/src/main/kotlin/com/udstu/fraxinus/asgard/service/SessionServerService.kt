package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.cache.*
import com.udstu.fraxinus.helheim.core.*
import com.udstu.fraxinus.helheim.core.store.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.config.*
import com.udstu.fraxinus.asgard.toProfileModel
import java.security.*
import java.util.*

class SessionServerService(
    private val authenticator: SessionAuthenticator,
    private val config: ServerConfig
) {

    suspend fun joinServer(req: JoinServerRequest, serverIp: String?) {
        if (req.serverId == null) throw AsgardException.IllegalArgumentException(AsgardException.NULL_SERVER_ID)
        if (req.selectedProfile == null) throw AsgardException.IllegalArgumentException(AsgardException.NULL_SELECTED_PROFILE)

        val token = authenticate(req.accessToken, null)

        if (token.boundCharacter == null || token.boundCharacter!!.id != req.selectedProfile)
            throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_PROFILE)

        authenticator.joinServer(token, req.serverId, serverIp)
    }

    suspend fun hasJoinedServer(serverId: String?, username: String?, ip: String?): ProfileModel? {
        if (serverId != null && username != null) {
            return authenticator.verifyUser(username, serverId, ip)?.toProfileModel()?.let {
                it.apply {
                    properties = generateSignedProperties(properties!!)
                }
            }
        }

        return null
    }

    suspend fun findProfile(profileId: String?, unsigned: Boolean = true): ProfileModel? {
        if (profileId == null) return null

        return CharacterStore.getCharacterById(profileId, false)?.run {
            toProfileModel().let {
                it.apply {
                    if (!unsigned) {
                        properties = generateSignedProperties(properties!!)
                    }
                }
            }
        }
    }

    private fun generateSignedProperties(properties: List<Map<String, String>>): List<Map<String, String>> {
        return properties.map { property ->
            val signature = Signature.getInstance("SHA1withRSA")
            signature.initSign(config.privateKey)
            signature.update(property.getValue("value").toByteArray())

            mapOf(
                "name" to property.getValue("name"),
                "value" to property.getValue("value"),
                "signature" to Base64.getEncoder().encodeToString(signature.sign())
            )
        }
    }

    suspend fun queryProfiles(req: List<String>) : List<ProfileModel> {
        return req.distinct().mapNotNull {
            CharacterStore.getCharacterByName(it)?.toProfileModel()
        }
    }

    private suspend fun authenticate(accessToken: String?, clientToken: String?): Token {
        if (accessToken == null) {
            throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        }

        return TokenStore.authenticate(accessToken, clientToken) ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
    }
}
