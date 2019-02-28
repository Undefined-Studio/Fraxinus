package com.udstu.fraxinus.asgard.core.store

import com.udstu.fraxinus.asgard.core.*
import com.udstu.fraxinus.asgard.dao.*
import com.udstu.fraxinus.asgard.dao.entity.*
import com.udstu.fraxinus.asgard.exception.*
import com.udstu.fraxinus.asgard.util.*
import org.slf4j.*

object TokenStore {
    private val logger = LoggerFactory.getLogger(TokenStore::class.java)!!
    private const val MS_OF_WEEK = 604_800_000L
//    private const val MS_OF_FORTNIGHT = 1_209_600_000L

    suspend fun acquireToken(user: User, clientToken: String? = null, selectedCharacter: Character? = null): Token {
        val accessToken = randomUnsignedUUID()

        val token = if (selectedCharacter == null) {
            if (user.characters.size == 1) {
                Token(
                    clientToken?: randomUnsignedUUID(),
                    accessToken,
                    user.characters.first(),
                    user,
                    System.currentTimeMillis()
                )
            } else {
                Token(
                    clientToken?: randomUnsignedUUID(),
                    accessToken,
                    null,
                    user,
                    System.currentTimeMillis()
                )
            }
        } else {
            if (user.characters.none {
                    it.name == selectedCharacter.name && it.id == selectedCharacter.id
                })
                throw AsgardException.IllegalArgumentException(AsgardException.PROFILE_NOT_FOUND)

            Token(
                clientToken?: randomUnsignedUUID(),
                accessToken,
                selectedCharacter,
                user,
                System.currentTimeMillis()
            )
        }

        // Save Token
        save(token).also {
            logger.info("Add new token for user(id: ${user.id})")
        }

        return token
    }

    suspend fun authenticate(accessToken: String, clientToken: String?): Token? {
        val token = getToken(accessToken) ?: throw AsgardException.ForbiddenOperationException(AsgardException.INVALID_TOKEN)

        if (System.currentTimeMillis() > token.createdTime + MS_OF_WEEK) {
            consume(token)
            logger.info("Consume expired token(${token.accessToken})")
            return null
        }

        if (clientToken != null && clientToken != token.clientToken) {
            return null
        }

        return token
    }

    suspend fun authenticateAndConsume(accessToken: String, clientToken: String?): Token? {
        return authenticate(accessToken, clientToken)?.also {
            consume(it)
            logger.info("Consume token(${it.accessToken})")
        }
    }

    suspend fun invokeAllToken(user: User) {
        Tokens.removeByUserId(user.id)
    }

    private suspend fun consume(token: Token) {
        Tokens.removeByAccessToken(token.accessToken)
    }


    private suspend fun save(token: Token) {
        Tokens.save(TokenEntity(
            null,
            token.accessToken,
            token.clientToken,
            token.boundCharacter?.id,
            token.createdTime,
            token.user.id
        ))
    }

    private suspend fun getToken(accessToken: String): Token? {
        return Tokens.findByAccessToken(accessToken)?.let {
            val user = UserStore.getUser(it.userId)
            if (user == null) {
                null
            } else {
                val boundCharacter  = if (it.profileId != null) {
                    CharacterStore.getCharacterById(it.profileId)
                } else {
                    null
                }

                Token(
                    it.clientToken,
                    it.accessToken,
                    boundCharacter,
                    user,
                    it.createdTime
                )
            }
        }
    }
}