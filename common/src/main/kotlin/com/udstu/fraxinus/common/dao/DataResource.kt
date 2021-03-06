package com.udstu.fraxinus.common.dao

import com.zaxxer.hikari.*
import org.jetbrains.exposed.sql.*

object DataResource {
    fun init(databaseUrl: String, username: String, password: String) {
        Database.connect(HikariDataSource(HikariConfig().apply {
            driverClassName = "com.mysql.jdbc.Driver"
            maximumPoolSize = 5
            jdbcUrl = databaseUrl
            this.username = username
            this.password = password
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }.also {
            it.validate()
        }))
    }
}
