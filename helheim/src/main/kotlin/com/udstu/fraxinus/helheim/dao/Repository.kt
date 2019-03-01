package com.udstu.fraxinus.helheim.dao

import org.jetbrains.exposed.sql.*

interface Repository<T, ID> {
    fun generateEntity(result: ResultRow): T
    suspend fun find(id: ID): T?
    suspend fun findAll(): Collection<T>
    suspend fun save(t: T): ID
    suspend fun update(t: T)
    suspend fun remove(id: ID)
    suspend fun removeAll()
}
