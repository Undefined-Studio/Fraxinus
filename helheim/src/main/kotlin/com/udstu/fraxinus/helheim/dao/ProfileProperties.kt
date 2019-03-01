package com.udstu.fraxinus.helheim.dao

import com.udstu.fraxinus.helheim.dao.entity.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*

object ProfileProperties : IntIdTable("profile_property"), Repository<ProfilePropertyEntity, Int> {
    val name = varchar("name", 50)
    val timestamp = date("timestamp")
    val profileId = varchar("profile_id", 40)
    val profileName = varchar("profile_name", 40)

    override fun generateEntity(result: ResultRow): ProfilePropertyEntity {
        return ProfilePropertyEntity(
            result[ProfileProperties.id].value,
            result[ProfileProperties.name],
            result[ProfileProperties.timestamp].toDate(),
            result[ProfileProperties.profileId],
            result[ProfileProperties.profileName]
        )
    }

    override suspend fun find(id: Int): ProfilePropertyEntity? {
        return query {
            ProfileProperties.select {
                ProfileProperties.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    suspend fun findByProfileId(id: String): List<ProfilePropertyEntity> {
        return query {
            ProfileProperties.select {
                ProfileProperties.profileId eq id
            }.map {
                generateEntity(it)
            }
        }
    }

    override suspend fun findAll(): Collection<ProfilePropertyEntity> {
        return query {
            ProfileProperties.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun save(t: ProfilePropertyEntity): Int {
        return query {
            ProfileProperties.insertAndGetId {
                it[name] = t.name
                it[timestamp] = DateTime(t.timestamp)
                it[profileId] = t.profileId
                it[profileName] = t.profileName
            }.value
        }
    }

    override suspend fun update(t: ProfilePropertyEntity) {
        query {
            ProfileProperties.update({
                ProfileProperties.id eq t.id
            }) {
                it[ProfileProperties.name] = t.name
                it[ProfileProperties.timestamp] = DateTime(t.timestamp)
                it[ProfileProperties.profileId] = t.profileId
                it[ProfileProperties.profileName] = t.profileName
            }
        }
    }

    override suspend fun remove(id: Int) {
        query {
            ProfileProperties.deleteWhere {
                ProfileProperties.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            ProfileProperties.deleteAll()
        }
    }
}
