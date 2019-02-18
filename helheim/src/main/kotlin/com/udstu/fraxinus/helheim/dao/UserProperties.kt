package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object UserProperties : Table("user_property") {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 50)
    val value = varchar("value", 255)
    val userId = varchar("user_id", 40)
}
