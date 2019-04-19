package com.udstu.fraxinus.common.dao

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.*

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        block()
    }
}
