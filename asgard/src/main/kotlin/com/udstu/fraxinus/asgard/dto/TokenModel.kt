package com.udstu.fraxinus.asgard.dto

import org.joda.time.DateTime

class TokenModel(
    val accessToken: String,
    val clientToken: String,
    val selectedCharacter: String?,
    val userId: String,
    val createdTime: DateTime
) {

    fun isComleteValid(): Boolean {
        if (System.currentTimeMillis() > createdTime.millis + 7 * 24 * 3600 * 1000L) {
            return true
        }

        return false
    }

    fun isFullyExpired(): Boolean {
        if (System.currentTimeMillis() > createdTime.millis + 14 * 24 * 3600 * 1000L) {
            return true
        }

        return false
    }
}
