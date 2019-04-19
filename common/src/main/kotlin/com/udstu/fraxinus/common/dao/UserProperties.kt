package com.udstu.fraxinus.common.dao

import com.udstu.fraxinus.common.dao.entity.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*


object UserProperties : IntIdTable("user_property"), Repository<UserPropertyEntity, Int> {
    val name = varchar("name", 50)
    val value = varchar("value", 255)
    val userId = varchar("user_id", 40)

    override fun generateEntity(result: ResultRow): UserPropertyEntity {
        return UserPropertyEntity(
            result[UserProperties.id].value,
            result[UserProperties.name],
            result[UserProperties.value],
            result[UserProperties.userId]
        )
    }

    override suspend fun find(id: Int): UserPropertyEntity? {
        return query {
            UserProperties.select {
                UserProperties.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    suspend fun findByUserId(id: String): List<UserPropertyEntity> {
        return query {
            UserProperties.select {
                UserProperties.userId eq id
            }.map {
                generateEntity(it)
            }
        }
    }

    override suspend fun findAll(): Collection<UserPropertyEntity> {
        return query {
            UserProperties.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun save(t: UserPropertyEntity): Int {
        return query {
            UserProperties.insertAndGetId {
                it[name] = t.name
                it[value] = t.value
                it[userId] = t.userId
            }.value
        }
    }

    override suspend fun update(t: UserPropertyEntity) {
        query {
            UserProperties.update({ UserProperties.id eq t.id }){
                it[UserProperties.name] = t.name
                it[UserProperties.value] = t.value
                it[UserProperties.userId] = t.userId
            }
        }
    }

    override suspend fun remove(id: Int) {
        query {
            UserProperties.deleteWhere {
                UserProperties.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            UserProperties.deleteAll()
        }
    }
}
