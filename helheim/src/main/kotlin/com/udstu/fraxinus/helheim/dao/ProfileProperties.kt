package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.dao.*

object ProfileProperties : IntIdTable("profile_property") {
    val name = varchar("name", 50)
    val timestamp = date("timestamp")
    val profileId = varchar("profile_id", 40)
    val profileName = varchar("profile_name", 40)
}
