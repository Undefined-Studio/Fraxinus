package com.udstu.fraxinus.asgard.dto

data class LoginRequest(
    val username: String,
    val password: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
    val agent: Agent) {
    companion object {
        data class Agent(val name: String, val version: Int)
    }
}
