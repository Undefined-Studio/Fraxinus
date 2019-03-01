package com.udstu.fraxinus.helheim.dao

import com.udstu.fraxinus.helheim.dao.entity.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.joda.time.*

object Capes : IntIdTable("cape"), Repository<CapeEntity, Int> {
    val name = varchar("name", 40)
    val url = varchar("url", 255)
    val uploadTime = datetime("upload_time")
    val modifiedTime = datetime("modified_time")

    override suspend fun find(id: Int): CapeEntity? {
        return query {
            Capes.select {
                Capes.id eq id
            }.limit(1).map {
                generateEntity(it)
            }.firstOrNull()
        }
    }

    override suspend fun findAll(): Collection<CapeEntity> {
        return query {
            Capes.selectAll().map {
                generateEntity(it)
            }
        }
    }

    override suspend fun update(t: CapeEntity) {
        query {
            Capes.update({
                Capes.id eq t.id
            }) {
                it[Capes.name] = t.name
                it[Capes.url] = t.url
                it[Capes.uploadTime] = DateTime(t.uploadTime)
                it[Capes.modifiedTime] = DateTime(t.modifiedTime)
            }
        }
    }

    override fun generateEntity(result: ResultRow): CapeEntity {
        return CapeEntity(
            result[Capes.id].value,
            result[Capes.name],
            result[Capes.url],
            result[Capes.uploadTime].toDate(),
            result[Capes.modifiedTime].toDate()
        )
    }

    override suspend fun save(t: CapeEntity): Int {
        return query {
            Capes.insertAndGetId {
                it[name] = t.name
                it[url] = t.url
                it[uploadTime] = DateTime(t.uploadTime)
                it[modifiedTime] = DateTime(t.modifiedTime)
            }.value
        }
    }

    override suspend fun remove(id: Int) {
        query {
            Capes.deleteWhere {
                Capes.id eq id
            }
        }
    }

    override suspend fun removeAll() {
        query {
            Capes.deleteAll()
        }
    }

}
