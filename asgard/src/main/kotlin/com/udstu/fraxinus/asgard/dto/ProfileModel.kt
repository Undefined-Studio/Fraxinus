package com.udstu.fraxinus.asgard.dto

data class ProfileModel(val id: String, val name: String, val properties: List<ProfilePropertyModel>? = null)
