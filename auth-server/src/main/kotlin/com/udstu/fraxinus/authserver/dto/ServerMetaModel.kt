package com.udstu.fraxinus.authserver.dto

data class ServerMetaModel(
    val meta: Map<String, String>,
    val skinDomains: List<String>,
    val signaturePublickey: String
)
