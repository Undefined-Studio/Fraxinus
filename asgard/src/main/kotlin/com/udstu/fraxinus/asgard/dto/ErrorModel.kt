package com.udstu.fraxinus.asgard.dto

data class ErrorModel(val error: String, val errorMessage: String, val cause: Any? = null)
