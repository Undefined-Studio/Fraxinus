package com.udstu.fraxinus.common.dao.entity

class TokenEntity(
    val id: Int?,
    val accessToken: String,
    val clientToken: String,
    val profileId: String?,
    val createdTime: Long,
    val userId: String
)
