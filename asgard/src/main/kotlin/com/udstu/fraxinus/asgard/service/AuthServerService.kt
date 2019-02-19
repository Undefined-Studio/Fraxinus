package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.AsgardException
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.util.encodeMD5
import com.udstu.fraxinus.helheim.util.randomUsignedUUID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

class AuthServerService {
    suspend fun authenticate(req: LoginRequest): LoginResponse {
        // 查询用户
        val user = query {
            Users.select {
                Users.username eq req.username and (Users.password eq req.password.encodeMD5())
            }.firstOrNull()?.let {
                generateUserModel(it)
            }
        } ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_CREDENTIALS)

        val token = if (req.clientToken == null) {
            acquireToken(user, randomUsignedUUID())
        } else {
            acquireToken(user, req.clientToken)
        }.also { model ->
            val id = Tokens.insertAndGetId {
                it[accessToken] = model.accessToken
                it[clientToken] = model.clientToken
                it[profileId] = model.selectedCharacter
                it[createdTime] = DateTime.now()
                it[userId] = model.userId
            }
            logger.info("Add new token(id: $id) for user(id: ${model.userId})")
        }

        val availableProfiles = query {
            Profiles.select {
                Profiles.userId eq user.id
            }.map {
                generateProfileModel(it)
            }
        }

        if (token.selectedCharacter != null) {
            return LoginResponse(
                token.accessToken,
                token.clientToken,
                availableProfiles,
                availableProfiles.firstOrNull {
                    it.id == token.selectedCharacter
                } ?: throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
            )
        }

        return LoginResponse(
            token.accessToken,
            token.clientToken,
            availableProfiles
        )
    }

    suspend fun refresh(req: RefreshTokenRequest): RefreshTokenResponse {
        if (req.selectedProfile != null) {
            val name = query {
                Profiles.select {
                    Profiles.id eq req.selectedProfile.id
                }.map {
                    it[Profiles.name]
                }.firstOrNull() ?: throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
            }

            if (name != req.selectedProfile.name) throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
        }

        val oldToken = refreshToken(req.accessToken, req.clientToken)

        val user = query {
            Users.select {
                Users.id eq oldToken.userId
            }.first().let {
                generateUserModel(it)
            }
        }

        val newToken = acquireToken(
            user,
            oldToken.clientToken,
            if (req.selectedProfile == null) {
                null
            } else {
                req.selectedProfile.id
            }
        )

        if (req.requestUser) {
            return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, req.selectedProfile, user)
        }

        return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, req.selectedProfile)
    }

    private suspend fun acquireToken(user: UserModel, clientToken: String, profileId: String? = null): TokenModel {
        val accessToken = randomUsignedUUID()
        if (profileId == null) {
            val profileIds = query {
                Profiles.select {
                    Profiles.userId eq user.id
                }.map {
                    it[Profiles.id]
                }
            }
            return if (profileIds.size == 1) {
                TokenModel(accessToken, clientToken, profileIds.first(), user.id, DateTime.now())
            } else {
                TokenModel(accessToken, clientToken, null, user.id, DateTime.now())
            }
        } else {
            val userId = query {
                Profiles.select { Profiles.id eq profileId }.map{
                    it[Profiles.userId]
                }.firstOrNull() ?: throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
            }
            return if (userId == user.id) {
                TokenModel(accessToken, clientToken, profileId, user.id, DateTime.now())
            } else {
                throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
            }
        }
    }

    private suspend fun refreshToken(accessToken: String?, clientToken: String?): TokenModel {
        if (accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)

        // 检查token是否合法
        val token = query {
            Tokens.select {
                Tokens.accessToken eq accessToken
            }.map {

                if (clientToken != null && clientToken != it[Tokens.clientToken]) {
                    throw AsgardException.IllegalArgumentException(AsgardException.INVALID_TOKEN)
                }

                TokenModel(
                    it[Tokens.accessToken],
                    it[Tokens.clientToken],
                    it[Tokens.profileId],
                    it[Tokens.userId],
                    it[Tokens.createdTime]
                )
            }.firstOrNull() ?: throw AsgardException.IllegalArgumentException(AsgardException.INVALID_TOKEN)
        }

        query {
            Tokens.deleteWhere {
                Tokens.accessToken eq accessToken
            }
        }

        if (token.isFullyExpired()) throw AsgardException.IllegalArgumentException(AsgardException.INVALID_TOKEN)

        return token
    }

    companion object {
        val logger = LoggerFactory.getLogger(AuthServerService::class.java)!!
    }
}