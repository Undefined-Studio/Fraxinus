package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.util.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*

class TokenStore {
    suspend fun acquireToken(user: UserModel, clientToken: String, profile: ProfileModel? = null): TokenModel {
        val accessToken = randomUsignedUUID()
        val profiles = query {
            Profiles.select {
                Profiles.userId eq user.id
            }.map {
                generateProfileSimpleModel(it)
            }
        }

        return if (profile == null) {
            if (profiles.size == 1) {
                TokenModel(accessToken, clientToken, profiles.first(), user, DateTime.now())
            } else {
                TokenModel(accessToken, clientToken, null, user, DateTime.now())
            }
        } else {
            if (!profiles.contains(profile)) throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)

            TokenModel(accessToken, clientToken, profile, user, DateTime.now())
        }
    }

    suspend fun validateAndConsumeToken(accessToken: String?, clientToken: String?): TokenModel {
        if (accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)

        // 检查token是否合法
        val token = validateToken(accessToken, clientToken)

        consumeToken(token.accessToken)

        if (token.isFullyExpired()) throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)

        return token
    }

    suspend fun validateToken(accessToken: String, clientToken: String?): TokenModel {
        return query {
            Tokens.select {
                Tokens.accessToken eq accessToken
            }.map {

                val user = Users.select {
                    Users.id eq it[Tokens.userId]
                }.map { result ->
                    generateUserModel(result)
                }.firstOrNull() ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)

                val profileId = it[Tokens.profileId]

                val profile = if (profileId == null) {
                    null
                } else {
                    Profiles.select {
                        Profiles.id eq profileId
                    }.map { result ->
                        generateProfileSimpleModel(result)
                    }.firstOrNull() ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
                }

                if (clientToken != null && clientToken != it[Tokens.clientToken]) {
                    throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
                }

                TokenModel(
                    it[Tokens.accessToken],
                    it[Tokens.clientToken],
                    profile,
                    user,
                    it[Tokens.createdTime]
                )
            }.firstOrNull() ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
        }
    }

    private suspend fun consumeToken(accessToken: String) {
        query {
            Tokens.deleteWhere {
                Tokens.accessToken eq accessToken
            }
        }
    }

    suspend fun saveToken(model: TokenModel) {
        query {
            val id = Tokens.insertAndGetId {
                it[accessToken] = model.accessToken
                it[clientToken] = model.clientToken
                it[profileId] = model.selectedCharacter?.id
                it[createdTime] = DateTime.now()
                it[userId] = model.user.id
            }
            AuthServerService.logger.info("Add new token(id: $id) for user(id: ${model.user.id})")
        }
    }
}