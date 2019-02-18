package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object Users : Table("user") {
    val id = varchar("id", 40).primaryKey()
    val username = varchar("username", 40).uniqueIndex()
    val password = varchar("password", 255)
}
