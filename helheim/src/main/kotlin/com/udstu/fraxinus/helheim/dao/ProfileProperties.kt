package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object ProfileProperties : Table("profile_property") {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 50)
    val timestamp = date("timestamp")
    val profileId = varchar("profile_id", 40)
    val profileName = varchar("profile_name", 40)
}
