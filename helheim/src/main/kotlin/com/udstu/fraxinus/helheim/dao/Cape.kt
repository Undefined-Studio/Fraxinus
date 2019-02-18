package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

object Cape : Table("cape") {
    val id = Skins.integer("id").primaryKey()
    val name = Skins.varchar("name", 40)
    val url = Skins.varchar("url", 255)
    val uploadTime = Skins.datetime("upload_time")
    val modifiedTime = Skins.datetime("modified_time")
}
