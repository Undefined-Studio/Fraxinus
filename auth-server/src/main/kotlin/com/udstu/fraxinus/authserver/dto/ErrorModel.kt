package com.udstu.fraxinus.authserver.dto

data class ErrorModel(val error: String, val errorMessage: String, val cause: Any? = null)
