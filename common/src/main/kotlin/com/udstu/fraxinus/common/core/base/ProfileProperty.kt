package com.udstu.fraxinus.helheim.core.base

abstract class ProfileProperty(val name: String, val timestamp: Long, val profileName: String, val profileId: String) {

    abstract fun generateValue(): String

}
