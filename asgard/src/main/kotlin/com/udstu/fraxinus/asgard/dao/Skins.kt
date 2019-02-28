package com.udstu.fraxinus.asgard.dao

import com.udstu.fraxinus.asgard.dao.entity.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*

object Skins : IntIdTable("skin"), Repository<SkinEntity, Int> {
    val name = varchar("name", 40)
    val url = varchar("url", 255)
    val model = varchar("model", 20)
    val uploadTime = datetime("upload_time")
    val modifiedTime = datetime("modified_time")

    override fun generateEntity(result: ResultRow): SkinEntity {
        return SkinEntity(
            result[Skins.id].value,
            result[Skins.name],
            result[Skins.url],
            result[Skins.model],
            result[Skins.uploadTime].toDate(),
            result[Skins.modifiedTime].toDate()
        )
    }

    override suspend fun find(id: Int): SkinEntity? {
        return query {
            Skins.select {
                Skins.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    override suspend fun findAll(): Collection<SkinEntity> {
        return query {
            Skins.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun save(t: SkinEntity): Int {
        return query {
            Skins.insertAndGetId {
                it[name] = t.name
                it[url] = t.url
                it[model] = t.model
                it[uploadTime] = DateTime(t.uploadTime)
                it[modifiedTime] = DateTime(t.modifiedTime)
            }.value
        }
    }

    override suspend fun update(t: SkinEntity) {
        query {
            Skins.update({ Skins.id eq t.id }) {
                it[Skins.name] = t.name
                it[Skins.url] = t.url
                it[Skins.model] = t.model
                it[Skins.uploadTime] = DateTime(t.uploadTime)
                it[Skins.modifiedTime] = DateTime(t.modifiedTime)
            }
        }
    }

    override suspend fun remove(id: Int) {
        query {
            Skins.deleteWhere {
                Skins.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            Skins.deleteAll()
        }
    }
}
