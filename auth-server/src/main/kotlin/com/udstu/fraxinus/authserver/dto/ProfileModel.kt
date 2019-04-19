package com.udstu.fraxinus.asgard.dto

data class ProfileModel(val id: String, val name: String, var properties: List<Map<String, String>>? = null)
