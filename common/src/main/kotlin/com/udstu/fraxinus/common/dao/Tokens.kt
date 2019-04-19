package com.udstu.fraxinus.common.dao

import com.udstu.fraxinus.common.dao.entity.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*

object Tokens : IntIdTable("token"), Repository<TokenEntity, Int> {
    val accessToken = varchar("access_token", 40)
    val clientToken = varchar("client_token", 40)
    val profileId = varchar("profile_id", 40).nullable()
    val createdTime = datetime("created_time")
    val userId = varchar("user_id", 40)

    override fun generateEntity(result: ResultRow): TokenEntity {
        return TokenEntity(
            result[Tokens.id].value,
            result[Tokens.accessToken],
            result[Tokens.clientToken],
            result[Tokens.profileId],
            result[Tokens.createdTime].millis,
            result[Tokens.userId]
        )
    }

    override suspend fun find(id: Int): TokenEntity? {
        return query {
            Tokens.select {
                Tokens.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    suspend fun findByAccessToken(accessToken: String): TokenEntity? {
        return query {
            Tokens.select {
                Tokens.accessToken eq accessToken
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    override suspend fun findAll(): Collection<TokenEntity> {
        return query {
            Tokens.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun save(t: TokenEntity): Int {
        return query {
            Tokens.insertAndGetId {
                it[accessToken] = t.accessToken
                it[clientToken] = t.clientToken
                it[profileId] = t.profileId
                it[userId] = t.userId
                it[createdTime] = DateTime(t.createdTime)
            }.value
        }
    }

    override suspend fun update(t: TokenEntity) {
        query {
            Tokens.update({ Tokens.id eq t.id }) {
                it[Tokens.accessToken] = t.accessToken
                it[Tokens.clientToken] = t.clientToken
                it[Tokens.profileId] = t.profileId
                it[Tokens.userId] = t.userId
                it[Tokens.createdTime] = DateTime(t.createdTime)
            }
        }
    }

    suspend fun removeByAccessToken(accessToken: String) {
        query {
            Tokens.deleteWhere {
                Tokens.accessToken eq accessToken
            }
        }
    }

    override suspend fun remove(id: Int) {
        query {
            Tokens.deleteWhere {
                Tokens.id eq id
            }
        }
    }

    suspend fun removeByUserId(id: String) {
        query {
            Tokens.deleteWhere {
                Tokens.userId eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            Tokens.deleteAll()
        }
    }
}
