package com.udstu.fraxinus.asgard.service

import com.udstu.fraxinus.asgard.core.*
import com.udstu.fraxinus.asgard.core.store.*
import com.udstu.fraxinus.asgard.dto.*
import com.udstu.fraxinus.asgard.exception.*
import org.slf4j.*

class AuthServerService {
    suspend fun login(req: LoginRequest): LoginResponse {
        // 查询用户
        val user = UserStore.getAuthenticatedUser(req.username, req.password)?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_CREDENTIALS)

        val token = TokenStore.acquireToken(user, req.clientToken)

        if (req.requestUser) {
            return LoginResponse(
                token.accessToken,
                token.clientToken,
                user.characters.map {
                    it.toProfileModel()
                },
                token.boundCharacter?.run {
                    toProfileModel()
                },
                user.toUserModel()
            )
        }

        return LoginResponse(
            token.accessToken,
            token.clientToken,
            user.characters.map {
                it.toProfileModel()
            },
            token.boundCharacter?.run {
                toProfileModel()
            }
        )
    }

    suspend fun refresh(req: RefreshTokenRequest): RefreshTokenResponse {
        val characterToSelect = if (req.selectedProfile == null) {
            null
        } else {
            CharacterStore.getCharacterById(req.selectedProfile.id)?.also {
                if (it.name != req.selectedProfile.name) {
                    throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
                }
            }?: throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)
        }

        val oldToken = authenticateAndConsume(req.accessToken, req.clientToken)

        val newToken = TokenStore.acquireToken(
            oldToken.user,
            oldToken.clientToken,
            characterToSelect ?: oldToken.boundCharacter
        )

        if (req.requestUser) {
            return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, newToken.boundCharacter?.toProfileModel(), newToken.user.toUserModel())
        }

        return RefreshTokenResponse(newToken.accessToken, newToken.clientToken, newToken.boundCharacter?.toProfileModel())
    }

    suspend fun validate(req: ValidateRequest) {
        authenticate(req.accessToken, req.clientToken)
    }

    suspend fun invalidate(req: InvalidateRequest) {
        if (req.accessToken == null) throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        TokenStore.authenticateAndConsume(req.accessToken, req.clientToken)
    }

    suspend fun signOut(req: SignOutRequest) {
        val user = UserStore.getAuthenticatedUser(req.username, req.password) ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_CREDENTIALS)

        // 吊销所有Token
        TokenStore.invokeAllToken(user)
    }

    private suspend fun authenticateAndConsume(accessToken: String?, clientToken: String?): Token {
        if (accessToken == null) {
            throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        }

        return TokenStore.authenticateAndConsume(accessToken, clientToken) ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
    }

    private suspend fun authenticate(accessToken: String?, clientToken: String?): Token {
        if (accessToken == null) {
            throw AsgardException.IllegalArgumentException(AsgardException.NO_CREDENTIALS)
        }

        return TokenStore.authenticate(accessToken, clientToken) ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)
    }

    companion object {
        val logger = LoggerFactory.getLogger(AuthServerService::class.java)!!
    }
}
