package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.dao.*

object Skins : IntIdTable("skin") {
    val name = varchar("name", 40)
    val url = varchar("url", 255)
    val model = varchar("model", 20)
    val uploadTime = datetime("upload_time")
    val modifiedTime = datetime("modified_time")
}
