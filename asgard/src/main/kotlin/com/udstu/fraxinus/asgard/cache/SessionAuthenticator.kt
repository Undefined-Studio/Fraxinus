package com.udstu.fraxinus.asgard.cache

import com.googlecode.concurrentlinkedhashmap.*
import com.udstu.fraxinus.asgard.core.*
import kotlinx.coroutines.*
import java.time.*

class SessionAuthenticator(private val expireDuration: Duration = Duration.ofSeconds(30L)) {

    private val store = ConcurrentLinkedHashMap.Builder<String, SessionAuthentication>()
        .maximumWeightedCapacity(MAX_AUTH_COUNT)
        .build()

    val storeCount get() = store.size

    private suspend fun <T> storeOps(block: ConcurrentLinkedHashMap<String, SessionAuthentication>.() -> T): T = withContext(Dispatchers.IO) {
        block(store)
    }

    suspend fun joinServer(token: Token, serverId: String, ip: String?) {
        storeOps {
            this[serverId] = SessionAuthentication(
                serverId,
                token,
                ip,
                System.currentTimeMillis()
            )
        }
    }

    suspend fun verifyUser(username: String, serverId: String, ip: String?): Character? {
        val auth = storeOps {
            remove(serverId)
        }

        if (auth == null
            || System.currentTimeMillis() > auth.createdTime + expireDuration.toMillis()
            || auth.token.boundCharacter == null
            || auth.token.boundCharacter.name != username
            || (ip != null && ip != auth.ip)) {
            return null
        }

        return auth.token.boundCharacter
    }

    companion object {
        private const val MAX_AUTH_COUNT = 100000L

        data class SessionAuthentication(
            val serverId: String,
            val token: Token,
            val ip: String?,
            val createdTime: Long
        )
    }
}