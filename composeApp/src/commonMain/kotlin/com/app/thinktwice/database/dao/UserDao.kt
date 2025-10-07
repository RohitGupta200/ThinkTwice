package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.database.ThinkTwiceDatabase
import com.app.thinktwice.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import com.app.thinktwice.database.utils.TimeProvider

class UserDao(private val database: ThinkTwiceDatabase) {

    suspend fun insert(username: String, email: String, firstName: String, lastName: String): Long {
        val currentTime = TimeProvider.currentTimeMillis()
        database.userQueries.insertUser(username, email, firstName, lastName, currentTime, currentTime)
        return database.userQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun getById(id: Long): User? {
        return database.userQueries.getUserById(id).executeAsOneOrNull()
    }

    suspend fun getByUsername(username: String): User? {
        return database.userQueries.getUserByUsername(username).executeAsOneOrNull()
    }

    suspend fun getByEmail(email: String): User? {
        return database.userQueries.getUserByEmail(email).executeAsOneOrNull()
    }

    fun getAllFlow(): Flow<List<User>> {
        return database.userQueries.getAllUsers().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getAll(): List<User> {
        return database.userQueries.getAllUsers().executeAsList()
    }

    suspend fun update(id: Long, username: String, email: String, firstName: String, lastName: String) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.userQueries.updateUser(username, email, firstName, lastName, currentTime, id)
    }

    suspend fun delete(id: Long) {
        database.userQueries.deleteUser(id)
    }

    suspend fun count(): Long {
        return database.userQueries.getUserCount().executeAsOne()
    }

    suspend fun deleteAll() {
        database.userQueries.deleteAllUsers()
    }
}