package com.udstu.fraxinus.helheim.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DataResource {
    fun init(databaseUrl: String, username: String, password: String) {
        Database.connect(HikariDataSource(HikariConfig().apply {
            driverClassName = "com.mysql.jdbc.Driver"
            maximumPoolSize = 3
            jdbcUrl = databaseUrl
            this.username = username
            this.password = password
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }.also {
            it.validate()
        }))
    }

    fun <T> query(block: () -> T): T {
        return transaction {
            block()
        }
    }
}