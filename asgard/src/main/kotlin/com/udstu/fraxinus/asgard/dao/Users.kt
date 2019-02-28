package com.udstu.fraxinus.asgard.dao

import com.udstu.fraxinus.asgard.dao.entity.*
import org.jetbrains.exposed.sql.*

object Users : Table("user"), Repository<UserEntity, String> {
    val id = varchar("id", 40).primaryKey()
    val username = varchar("username", 40).uniqueIndex()
    val password = varchar("password", 255)

    override fun generateEntity(result: ResultRow): UserEntity {
        return UserEntity(
            result[Users.id],
            result[Users.username],
            result[Users.password]
        )
    }

    override suspend fun save(t: UserEntity): String {
        return query {
            Users.insert {
                it[id] = t.id
                it[username] = t.username
                it[password] = t.password
            } get Users.id
        }!!
    }

    override suspend fun find(id: String): UserEntity? {
        return query {
            Users.select {
                Users.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    suspend fun findByUsernameAndPassword(username: String, password: String): UserEntity? {
        return query {
            Users.select {
                Users.username eq username and(Users.password eq password)
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    override suspend fun findAll(): Collection<UserEntity> {
        return query {
            Users.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun update(t: UserEntity) {
        query {
            Users.update({ Users.id eq Users.id}) {
                it[Users.username] = t.username
                it[Users.password] = t.password
            }
        }
    }

    override suspend fun remove(id: String) {
        query {
            Users.deleteWhere {
                Users.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            Users.deleteAll()
        }
    }
}
