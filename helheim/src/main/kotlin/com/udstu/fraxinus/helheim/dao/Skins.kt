package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object Skins : Table("skin") {
    val id = integer("id").primaryKey()
    val name = varchar("name", 40)
    val url = varchar("url", 255)
    val model = varchar("model", 20)
    val uploadTime = datetime("upload_time")
    val modifiedTime = datetime("modified_time")
}
