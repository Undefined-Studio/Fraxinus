package com.udstu.fraxinus.asgard.dao

import com.udstu.fraxinus.asgard.dao.entity.ProfileEntity
import org.jetbrains.exposed.sql.*

object Profiles : Table("profile"), Repository<ProfileEntity, String> {
    val id = varchar("id", 40).primaryKey()
    val name = varchar("name", 40).uniqueIndex()
    val userId = varchar("user_id", 40)
    val model = varchar("model", 20)
    val skinId = integer("skin_id").nullable()
    val capeId = integer("cape_id").nullable()

    override fun generateEntity(result: ResultRow): ProfileEntity {
        return ProfileEntity(
            result[Profiles.id],
            result[Profiles.name],
            result[Profiles.userId],
            result[Profiles.model],
            result[Profiles.skinId],
            result[Profiles.capeId]
        )
    }

    override suspend fun find(id: String): ProfileEntity? {
        return query {
            Profiles.select {
                Profiles.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    suspend fun findByUserId(id: String): List<ProfileEntity> {
        return query {
            Profiles.select {
                Profiles.userId eq id
            }.map {
                generateEntity(it)
            }
        }
    }

    suspend fun findByName(name: String): ProfileEntity? {
        return query {
            Profiles.select {
                Profiles.name eq name
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    override suspend fun findAll(): Collection<ProfileEntity> {
        return query {
            Profiles.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun save(t: ProfileEntity): String {
        return query {
            Profiles.insert {
                it[id] = t.id
                it[name] = t.name
                it[userId] = t.userId
                it[model] = t.model
                it[skinId] = t.skinId
                it[capeId] = t.capeId
            } get Profiles.id
        }!!
    }

    override suspend fun update(t: ProfileEntity) {
        query {
            Profiles.update({
                Profiles.id eq t.id
            }) {
                it[Profiles.id] = t.id
                it[Profiles.name] = t.name
                it[Profiles.userId] = t.userId
                it[Profiles.model] = t.model
                it[Profiles.skinId] = t.skinId
                it[Profiles.capeId] = t.capeId
            }
        }
    }

    override suspend fun remove(id: String) {
        query {
            Profiles.deleteWhere {
                Profiles.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            Profiles.deleteAll()
        }
    }
}
