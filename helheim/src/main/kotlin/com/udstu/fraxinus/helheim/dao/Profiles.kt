package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object Profiles : Table("profile") {
    val id = varchar("id", 40).primaryKey()
    val name = varchar("name", 40).uniqueIndex()
    val userId = varchar("user_id", 40)
    val model = varchar("model", 20)
    val skinId = integer("skin_id").nullable()
    val capeId = integer("cape_id").nullable()
}
