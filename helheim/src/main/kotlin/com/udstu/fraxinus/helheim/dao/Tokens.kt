package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.dao.IntIdTable

object Tokens : IntIdTable("token") {
    val accessToken = varchar("access_token", 40)
    val clientToken = varchar("client_token", 40)
    val profileId = varchar("profile_id", 40).nullable()
    val createdTime = datetime("created_time")
    val userId = varchar("user_id", 40)
}
