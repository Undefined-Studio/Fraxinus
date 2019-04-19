package com.udstu.fraxinus.authserver.dto

data class ProfileModel(val id: String, val name: String, var properties: List<Map<String, String>>? = null)
