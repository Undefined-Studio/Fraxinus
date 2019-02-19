package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.dao.*

object Capes : IntIdTable("cape") {
    val name = varchar("name", 40)
    val url = varchar("url", 255)
    val uploadTime = datetime("upload_time")
    val modifiedTime = datetime("modified_time")
}
