package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.util.encodeMD5
import com.udstu.fraxinus.helheim.util.randomUsignedUUID
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import org.slf4j.LoggerFactory

class AuthServerService {
    suspend fun authenticate(req: LoginRequest): LoginResponse {
        // 查询用户
        val user = passwordAuthenticated(req.username, req.password)

        val token = if (req.clientToken == null) {
            acquireToken(user, randomUsignedUUID())
        } else {
            acquireToken(user, req.clientToken)
        }.also {
            saveToken(it)
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

        val oldToken = validateAndConsumeToken(req.accessToken, req.clientToken)

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
        ).also {
            saveToken(it)
        }

        if (req.requestUser) {
            return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, req.selectedProfile, user)
        }

        return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, req.selectedProfile)
    }

    suspend fun validate(req: ValidateRequest) {
        validateToken(req.accessToken, req.clientToken)
    }

    suspend fun invalidate(req: InvalidateRequest) {
        if (req.accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        validateAndConsumeToken(req.accessToken, req.clientToken)
    }

    suspend fun signOut(req: SignOutRequest) {
        val user = passwordAuthenticated(req.username, req.password)

        // 吊销所有Token
        query {
            Tokens.deleteWhere {
                Tokens.userId eq user.id
            }
        }.also {
            logger.info("Revoke all tokens of User(id: ${user.id})")
        }
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

    private suspend fun validateAndConsumeToken(accessToken: String?, clientToken: String?): TokenModel {
        if (accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)

        // 检查token是否合法
        val token = validateToken(accessToken, clientToken)

        consumeToken(token.accessToken)

        if (token.isFullyExpired()) throw AsgardException.IllegalArgumentException(AsgardException.INVALID_TOKEN)

        return token
    }

    private suspend fun validateToken(accessToken: String, clientToken: String?): TokenModel {
        return query {
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
    }

    private suspend fun consumeToken(accessToken: String) {
        query {
            Tokens.deleteWhere {
                Tokens.accessToken eq accessToken
            }
        }
    }

    private suspend fun passwordAuthenticated(username: String, password: String): UserModel {
        return query {
            Users.select {
                Users.username eq username and (Users.password eq password.encodeMD5())
            }.firstOrNull()?.let {
                generateUserModel(it)
            }
        } ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_CREDENTIALS)
    }

    private suspend fun saveToken(model: TokenModel) {
        query {
            val id = Tokens.insertAndGetId {
                it[accessToken] = model.accessToken
                it[clientToken] = model.clientToken
                it[profileId] = model.selectedCharacter
                it[createdTime] = DateTime.now()
                it[userId] = model.userId
            }
            logger.info("Add new token(id: $id) for user(id: ${model.userId})")
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(AuthServerService::class.java)!!
    }
}
