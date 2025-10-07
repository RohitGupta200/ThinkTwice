package com.app.thinktwice.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import com.app.thinktwice.database.utils.TimeProvider

class DatabaseManager(driverFactory: DatabaseDriverFactory) {
    private val database = ThinkTwiceDatabase(driverFactory.createDriver())

    // User operations
    suspend fun insertUser(username: String, email: String, firstName: String, lastName: String): Long {
        val currentTime = TimeProvider.currentTimeMillis()
        database.userQueries.insertUser(username, email, firstName, lastName, currentTime, currentTime)
        return database.userQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun getUserById(id: Long): User? {
        return database.userQueries.getUserById(id).executeAsOneOrNull()
    }

    suspend fun getUserByUsername(username: String): User? {
        return database.userQueries.getUserByUsername(username).executeAsOneOrNull()
    }

    suspend fun getUserByEmail(email: String): User? {
        return database.userQueries.getUserByEmail(email).executeAsOneOrNull()
    }

    fun getAllUsersFlow(): Flow<List<User>> {
        return database.userQueries.getAllUsers().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getAllUsers(): List<User> {
        return database.userQueries.getAllUsers().executeAsList()
    }

    suspend fun updateUser(id: Long, username: String, email: String, firstName: String, lastName: String) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.userQueries.updateUser(username, email, firstName, lastName, currentTime, id)
    }

    suspend fun deleteUser(id: Long) {
        database.userQueries.deleteUser(id)
    }

    suspend fun getUserCount(): Long {
        return database.userQueries.getUserCount().executeAsOne()
    }

    // Note operations
    suspend fun insertNote(userId: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false): Long {
        val currentTime = TimeProvider.currentTimeMillis()
        database.noteQueries.insertNote(userId, title, content, category, if (isImportant) 1 else 0, currentTime, currentTime)
        return database.noteQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun getNoteById(id: Long): Note? {
        return database.noteQueries.getNoteById(id).executeAsOneOrNull()
    }

    suspend fun getNotesByUserId(userId: Long): List<Note> {
        return database.noteQueries.getNotesByUserId(userId).executeAsList()
    }

    fun getNotesByUserIdFlow(userId: Long): Flow<List<Note>> {
        return database.noteQueries.getNotesByUserId(userId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getNotesByCategory(category: String): List<Note> {
        return database.noteQueries.getNotesByCategory(category).executeAsList()
    }

    suspend fun getImportantNotes(): List<Note> {
        return database.noteQueries.getImportantNotes().executeAsList()
    }

    fun getImportantNotesFlow(): Flow<List<Note>> {
        return database.noteQueries.getImportantNotes().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun searchNotes(query: String): List<Note> {
        return database.noteQueries.searchNotes(query, query).executeAsList()
    }

    suspend fun getAllNotes(): List<Note> {
        return database.noteQueries.getAllNotes().executeAsList()
    }

    fun getAllNotesFlow(): Flow<List<Note>> {
        return database.noteQueries.getAllNotes().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun updateNote(id: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.noteQueries.updateNote(title, content, category, if (isImportant) 1 else 0, currentTime, id)
    }

    suspend fun deleteNote(id: Long) {
        database.noteQueries.deleteNote(id)
    }

    suspend fun deleteNotesByUserId(userId: Long) {
        database.noteQueries.deleteNotesByUserId(userId)
    }

    suspend fun getNoteCount(): Long {
        return database.noteQueries.getNoteCount().executeAsOne()
    }

    suspend fun getNoteCountByUser(userId: Long): Long {
        return database.noteQueries.getNoteCountByUser(userId).executeAsOne()
    }

    // Settings operations
    suspend fun insertOrUpdateSetting(userId: Long, key: String, value: String) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.settingsQueries.insertOrUpdateSetting(userId, key, value, currentTime, currentTime)
    }

    suspend fun getSettingByUserAndKey(userId: Long, key: String): Settings? {
        return database.settingsQueries.getSettingByUserAndKey(userId, key).executeAsOneOrNull()
    }

    suspend fun getSettingsByUserId(userId: Long): List<Settings> {
        return database.settingsQueries.getSettingsByUserId(userId).executeAsList()
    }

    fun getSettingsByUserIdFlow(userId: Long): Flow<List<Settings>> {
        return database.settingsQueries.getSettingsByUserId(userId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getAllSettings(): List<Settings> {
        return database.settingsQueries.getAllSettings().executeAsList()
    }

    suspend fun deleteSetting(userId: Long, key: String) {
        database.settingsQueries.deleteSetting(userId, key)
    }

    suspend fun deleteSettingsByUserId(userId: Long) {
        database.settingsQueries.deleteSettingsByUserId(userId)
    }

    // Transaction support
    suspend fun <T> transaction(body: suspend () -> T): T {
        return database.transactionWithResult<T> {
            runBlocking { body() }
        }
    }
}