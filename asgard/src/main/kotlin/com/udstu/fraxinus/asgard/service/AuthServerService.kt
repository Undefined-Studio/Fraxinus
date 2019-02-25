package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.helheim.dao.*
import com.udstu.fraxinus.helheim.util.encodeMD5
import com.udstu.fraxinus.helheim.util.randomUsignedUUID
import org.jetbrains.exposed.sql.*
import org.slf4j.*

class AuthServerService(
    private val tokenStore: TokenStore
) {
    suspend fun authenticate(req: LoginRequest): LoginResponse {
        // 查询用户
        val user = passwordAuthenticated(req.username, req.password)

        val token = if (req.clientToken == null) {
            tokenStore.acquireToken(user, randomUsignedUUID())
        } else {
            tokenStore.acquireToken(user, req.clientToken)
        }.also {
            tokenStore.saveToken(it)
        }

        val availableProfiles = query {
            Profiles.select {
                Profiles.userId eq user.id
            }.map {
                generateProfileSimpleModel(it)
            }
        }

        if (req.requestUser) {
            return LoginResponse(
                token.accessToken,
                token.clientToken,
                availableProfiles,
                token.selectedCharacter,
                user
            )
        }

        return LoginResponse(
            token.accessToken,
            token.clientToken,
            availableProfiles,
            token.selectedCharacter
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

        val oldToken = tokenStore.validateAndConsumeToken(req.accessToken, req.clientToken)

        val newToken = tokenStore.acquireToken(
            oldToken.user,
            oldToken.clientToken,
            req.selectedProfile
        ).also {
            tokenStore.saveToken(it)
        }

        if (req.requestUser) {
            return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, newToken.selectedCharacter, newToken.user)
        }

        return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, newToken.selectedCharacter)
    }

    suspend fun validate(req: ValidateRequest) {
        tokenStore.validateToken(req.accessToken, req.clientToken)
    }

    suspend fun invalidate(req: InvalidateRequest) {
        if (req.accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        tokenStore.validateAndConsumeToken(req.accessToken, req.clientToken)
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

    private suspend fun passwordAuthenticated(username: String, password: String): UserModel {
        return query {
            Users.select {
                Users.username eq username and (Users.password eq password.encodeMD5())
            }.firstOrNull()?.let {
                generateUserModel(it)
            }
        } ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_CREDENTIALS)
    }

    companion object {
        val logger = LoggerFactory.getLogger(AuthServerService::class.java)!!
    }
}
