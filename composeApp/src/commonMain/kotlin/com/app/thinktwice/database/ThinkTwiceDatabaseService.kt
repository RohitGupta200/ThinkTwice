package com.app.thinktwice.database

import com.app.thinktwice.database.dao.NoteDao
import com.app.thinktwice.database.dao.SettingsDao
import com.app.thinktwice.database.dao.UserDao
import com.app.thinktwice.database.repository.NoteRepository
import com.app.thinktwice.database.repository.SettingsRepository
import com.app.thinktwice.database.repository.UserRepository
import kotlinx.coroutines.runBlocking

class ThinkTwiceDatabaseService(driverFactory: DatabaseDriverFactory) {
    private val database = ThinkTwiceDatabase(driverFactory.createDriver())

    // DAOs
    private val userDao = UserDao(database)
    private val noteDao = NoteDao(database)
    private val settingsDao = SettingsDao(database)

    // Repositories
    val userRepository = UserRepository(userDao)
    val noteRepository = NoteRepository(noteDao)
    val settingsRepository = SettingsRepository(settingsDao)

    // Transaction support
    suspend fun <T> transaction(body: suspend () -> T): T {
        return database.transactionWithResult<T> {
            runBlocking { body() }
        }
    }

    // Database utilities

    suspend fun clearAllData() {
        transaction {
            database.noteQueries.deleteAllNotes()
            database.settingsQueries.deleteAllSettings()
            database.userQueries.deleteAllUsers()
        }
    }

    suspend fun getDatabaseInfo(): DatabaseInfo {
        return DatabaseInfo(
            userCount = userRepository.getUserCount(),
            noteCount = noteRepository.getNoteCount(),
            settingsCount = settingsRepository.getAllSettings().size.toLong()
        )
    }
}

data class DatabaseInfo(
    val userCount: Long,
    val noteCount: Long,
    val settingsCount: Long
)