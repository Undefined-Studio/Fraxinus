package com.udstu.fraxinus.asgard.dto

data class ServerMetaModel(
    val meta: Map<String, String>,
    val skinDomains: List<String>,
    val signaturePublickey: String
)
