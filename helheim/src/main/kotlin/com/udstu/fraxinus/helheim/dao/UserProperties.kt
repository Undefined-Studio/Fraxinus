package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.dao.*


object UserProperties : IntIdTable("user_property") {
    val name = varchar("name", 50)
    val value = varchar("value", 255)
    val userId = varchar("user_id", 40)
}
